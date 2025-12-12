package com.nlshakal.web4.service;

import com.nlshakal.web4.constants.SecurityConstants;
import com.nlshakal.web4.dto.*;
import com.nlshakal.web4.entity.User;
import com.nlshakal.web4.repository.UserRepository;
import com.nlshakal.web4.service.oauth.OAuthService;
import com.nlshakal.web4.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService loginAttemptService;
    private final CaptchaService captchaService;
    private final PasswordValidationService passwordValidationService;
    private final PasswordHashingService passwordHashingService;
    private final AuditService auditService;
    private final OAuthService oauthService;


    public AuthResponse register(LoginRequest request, String ipAddress, String userAgent) {
        String username = request.getUsername();
        String password = request.getPassword();

        if (userRepository.existsByUsername(username)) {
            auditService.logSuspiciousActivity(username, ipAddress, "Попытка повторной регистрации");
            throw new RuntimeException("Пользователь уже существует");
        }

        if (!passwordValidationService.isValid(password)) {
            String errorMessage = passwordValidationService.getValidationMessage(password);
            throw new RuntimeException(errorMessage);
        }

        String passwordHash = passwordHashingService.hash(password);

        User user = new User(username, passwordHash, "local");
        user = userRepository.save(user);

        auditService.logRegistration(username, ipAddress, "local");

        String token = jwtUtil.generateToken(user.getUsername(), user.getId());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .message("Регистрация успешна")
                .requiresCaptcha(false)
                .failedAttempts(0)
                .build();
    }

    public AuthResponse login(LoginRequest request, String ipAddress, String userAgent) {
        String username = request.getUsername();
        String password = request.getPassword();

        if (loginAttemptService.requiresCaptcha(username)) {
            if (request.getCaptchaToken() == null || !captchaService.verifyCaptcha(request.getCaptchaToken())) {
                auditService.logFailedLogin(username, ipAddress, "Неправильная капча", userAgent);
                throw new RuntimeException("Пожалуйста, пройдите проверку reCAPTCHA");
            }
        }

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            loginAttemptService.recordFailedAttempt(username);
            auditService.logFailedLogin(username, ipAddress, "Пользователь не существует", userAgent);

            return AuthResponse.builder()
                    .message("Неверные учетные данные")
                    .requiresCaptcha(loginAttemptService.requiresCaptcha(username))
                    .failedAttempts(loginAttemptService.getFailedAttempts(username))
                    .build();
        }

        User user = userOptional.get();

        if (user.getPasswordHash() == null) {
            auditService.logFailedLogin(username, ipAddress, "Попытка входа по паролю в соц. аккаунт", userAgent);
            throw new RuntimeException("Этот аккаунт зарегистрирован через соцсеть. Пожалуйста, войдите через " + user.getAuthProvider());
        }

        if (!passwordHashingService.verify(password, user.getPasswordHash())) {
            loginAttemptService.recordFailedAttempt(username);
            auditService.logFailedLogin(username, ipAddress, "Неверный пароль", userAgent);

            int attempts = loginAttemptService.getFailedAttempts(username);

            if (attempts >= SecurityConstants.SUSPICIOUS_ACTIVITY_THRESHOLD) {
                auditService.logSuspiciousActivity(username, ipAddress,
                    "Множественные неудачные попытки входа: " + attempts);
            }

            return AuthResponse.builder()
                    .message("Неверные учетные данные")
                    .requiresCaptcha(loginAttemptService.requiresCaptcha(username))
                    .failedAttempts(attempts)
                    .build();
        }

        loginAttemptService.resetAttempts(username);
        auditService.logSuccessfulLogin(username, ipAddress, userAgent);

        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .message("Вход выполнен успешно")
                .requiresCaptcha(false)
                .failedAttempts(0)
                .build();
    }

    public AuthResponse socialLogin(SocialLoginRequest request, String ipAddress, String userAgent) {
        String provider = request.getProvider();
        String email = oauthService.authenticateAndGetEmail(request.getCode(), provider);

        Optional<User> existingUser = userRepository.findByUsername(email);
        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
            auditService.logSuccessfulLogin(email, ipAddress, userAgent + " (OAuth " + provider + ")");
        } else {
            user = new User(email, null, provider);
            user = userRepository.save(user);
            auditService.logRegistration(email, ipAddress, provider);
        }

        loginAttemptService.resetAttempts(email);

        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .message("Вход через " + provider + " выполнен")
                .requiresCaptcha(false)
                .failedAttempts(0)
                .build();
    }
}