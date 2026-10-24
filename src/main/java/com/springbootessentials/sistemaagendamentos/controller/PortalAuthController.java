package com.springbootessentials.sistemaagendamentos.controller;

import com.springbootessentials.sistemaagendamentos.auth.ClientRegistrationRequest;
import com.springbootessentials.sistemaagendamentos.auth.DoctorProvisionRequest;
import com.springbootessentials.sistemaagendamentos.auth.LoginRequest;
import com.springbootessentials.sistemaagendamentos.auth.PortalAuthService;
import com.springbootessentials.sistemaagendamentos.auth.PortalPrincipal;
import com.springbootessentials.sistemaagendamentos.auth.PortalUserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PortalAuthController {
    private final PortalAuthService portalAuthService;
    private final SecurityContextRepository securityContextRepository;
    private final SessionAuthenticationStrategy sessionAuthenticationStrategy;
    private final CookieCsrfTokenRepository csrfTokenRepository;

    @GetMapping("/csrf")
    public java.util.Map<String, String> csrf(CsrfToken csrfToken) {
        return java.util.Map.of("token", csrfToken.getToken());
    }

    @PostMapping("/login")
    public PortalUserResponse login(@Valid @RequestBody LoginRequest loginRequest,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        return establishSession(portalAuthService.authenticate(loginRequest), request, response);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public PortalUserResponse register(@Valid @RequestBody ClientRegistrationRequest registration,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        return establishSession(portalAuthService.registerClient(registration), request, response);
    }

    @PostMapping("/provision-doctor")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void provisionDoctor(@Valid @RequestBody DoctorProvisionRequest provisionRequest,
                                @RequestHeader(value = "X-Clinic-Provisioning-Token", required = false) String token) {
        portalAuthService.provisionDoctor(provisionRequest, token);
    }

    @GetMapping("/me")
    public PortalUserResponse currentUser(@org.springframework.security.core.annotation.AuthenticationPrincipal PortalPrincipal principal) {
        return portalAuthService.currentUser(principal);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        csrfTokenRepository.saveToken(null, request, response);
    }

    private PortalUserResponse establishSession(PortalPrincipal principal,
                                                HttpServletRequest request,
                                                HttpServletResponse response) {
        var authentication = UsernamePasswordAuthenticationToken.authenticated(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + principal.role()))
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        sessionAuthenticationStrategy.onAuthentication(authentication, request, response);
        securityContextRepository.saveContext(context, request, response);
        return PortalUserResponse.from(principal);
    }
}
