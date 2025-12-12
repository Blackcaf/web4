package com.nlshakal.web4;

import com.nlshakal.web4.config.JwtProperties;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class Web4Application {

    static {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();

                System.setProperty(key, value);

                if ("JWT_SECRET".equals(key)) {
                    System.setProperty("jwt.secret", value);
                }
                if ("JWT_EXPIRATION".equals(key)) {
                    System.setProperty("jwt.expiration", value);
                }
                if ("CAPTCHA_SECRET".equals(key)) {
                    System.setProperty("captcha.secret", value);
                }
                if ("CAPTCHA_SITE_KEY".equals(key)) {
                    System.setProperty("captcha.site-key", value);
                }
            });

            System.out.println("Environment variables loaded from .env file");
            System.out.println("- jwt.secret: " + (System.getProperty("jwt.secret") != null && System.getProperty("jwt.secret").length() > 50 ? "✓" : "✗"));
            System.out.println("- captcha.secret: " + (System.getProperty("captcha.secret") != null ? "✓" : "✗"));
            System.out.println("- DB_URL: " + (System.getProperty("DB_URL") != null ? "✓" : "✗"));

        } catch (Exception e) {
            System.err.println("Warning: .env file not found, using system environment variables");
            System.err.println("Make sure all required environment variables are set!");
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Web4Application.class, args);
    }
}

