package com.nlshakal.web4.resource;

import com.nlshakal.web4.dto.*;
import com.nlshakal.web4.service.*;
import com.nlshakal.web4.util.HttpRequestHelper;
import com.nlshakal.web4.util.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class AuthResource {

    private final AuthService authService;
    private final RateLimitService rateLimitService;
    private final HttpRequestHelper requestHelper;
    private final ResponseBuilder responseBuilder;

    @Context
    private HttpServletRequest servletRequest;


    @POST
    @Path("/register")
    public Response register(LoginRequest request) {
        String ipAddress = requestHelper.extractClientIP(servletRequest);
        String userAgent = requestHelper.extractUserAgent(servletRequest);

        if (!rateLimitService.isAllowed(ipAddress)) {
            return responseBuilder.tooManyRequests();
        }

        try {
            AuthResponse response = authService.register(request, ipAddress, userAgent);
            rateLimitService.resetIP(ipAddress);

            NewCookie tokenCookie = new NewCookie.Builder("auth_token")
                    .value(response.getToken())
                    .path("/")
                    .maxAge(86400)
                    .httpOnly(true)
                    .sameSite(NewCookie.SameSite.STRICT)
                    .build();

            NewCookie usernameCookie = new NewCookie.Builder("username")
                    .value(response.getUsername())
                    .path("/")
                    .maxAge(86400)
                    .sameSite(NewCookie.SameSite.STRICT)
                    .build();

            return Response.ok(response)
                    .cookie(tokenCookie, usernameCookie)
                    .build();
        } catch (RuntimeException e) {
            return responseBuilder.badRequest(e.getMessage());
        }
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        String ipAddress = requestHelper.extractClientIP(servletRequest);
        String userAgent = requestHelper.extractUserAgent(servletRequest);

        if (!rateLimitService.isAllowed(ipAddress)) {
            return responseBuilder.tooManyRequests();
        }

        try {
            AuthResponse response = authService.login(request, ipAddress, userAgent);
            if (response.getToken() != null) {
                rateLimitService.resetIP(ipAddress);
            }

            NewCookie tokenCookie = new NewCookie.Builder("auth_token")
                    .value(response.getToken())
                    .path("/")
                    .maxAge(86400)
                    .httpOnly(true)
                    .sameSite(NewCookie.SameSite.STRICT)
                    .build();

            NewCookie usernameCookie = new NewCookie.Builder("username")
                    .value(response.getUsername())
                    .path("/")
                    .maxAge(86400)
                    .sameSite(NewCookie.SameSite.STRICT)
                    .build();

            return Response.ok(response)
                    .cookie(tokenCookie, usernameCookie)
                    .build();
        } catch (RuntimeException e) {
            return responseBuilder.unauthorized(e.getMessage());
        }
    }

    @POST
    @Path("/social")
    public Response socialLogin(SocialLoginRequest request) {
        String ipAddress = requestHelper.extractClientIP(servletRequest);
        String userAgent = requestHelper.extractUserAgent(servletRequest);

        if (!rateLimitService.isAllowed(ipAddress)) {
            return responseBuilder.tooManyRequests();
        }

        try {
            AuthResponse response = authService.socialLogin(request, ipAddress, userAgent);
            rateLimitService.resetIP(ipAddress);

            NewCookie tokenCookie = new NewCookie.Builder("auth_token")
                    .value(response.getToken())
                    .path("/")
                    .maxAge(86400)
                    .httpOnly(true)
                    .sameSite(NewCookie.SameSite.STRICT)
                    .build();

            NewCookie usernameCookie = new NewCookie.Builder("username")
                    .value(response.getUsername())
                    .path("/")
                    .maxAge(86400)
                    .sameSite(NewCookie.SameSite.STRICT)
                    .build();

            return Response.ok(response)
                    .cookie(tokenCookie, usernameCookie)
                    .build();
        } catch (RuntimeException e) {
            return responseBuilder.badRequest(e.getMessage());
        }
    }

    @POST
    @Path("/logout")
    public Response logout() {
        NewCookie tokenCookie = new NewCookie.Builder("auth_token")
                .value("")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .sameSite(NewCookie.SameSite.STRICT)
                .build();

        NewCookie usernameCookie = new NewCookie.Builder("username")
                .value("")
                .path("/")
                .maxAge(0)
                .sameSite(NewCookie.SameSite.STRICT)
                .build();

        return Response.ok()
                .cookie(tokenCookie, usernameCookie)
                .build();
    }
}