package com.springbootessentials.sistemaagendamentos.auth;

public record PortalUserResponse(Integer id, String accountType, String name, String email) {
    public static PortalUserResponse from(PortalPrincipal principal) {
        return new PortalUserResponse(
                principal.id(),
                principal.accountType().name(),
                principal.name(),
                principal.email()
        );
    }
}
