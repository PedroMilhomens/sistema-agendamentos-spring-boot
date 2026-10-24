package com.springbootessentials.sistemaagendamentos.auth;

import java.io.Serializable;

public record PortalPrincipal(Integer id, AccountType accountType, String name, String email) implements Serializable {
    public String role() {
        return accountType.name();
    }
}
