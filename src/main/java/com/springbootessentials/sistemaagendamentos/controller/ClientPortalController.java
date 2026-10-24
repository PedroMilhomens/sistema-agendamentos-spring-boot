package com.springbootessentials.sistemaagendamentos.controller;

import com.springbootessentials.sistemaagendamentos.auth.PortalPrincipal;
import com.springbootessentials.sistemaagendamentos.portal.AppointmentBookingRequest;
import com.springbootessentials.sistemaagendamentos.portal.AppointmentResponse;
import com.springbootessentials.sistemaagendamentos.portal.ClientProfileResponse;
import com.springbootessentials.sistemaagendamentos.portal.ClientProfileUpdateRequest;
import com.springbootessentials.sistemaagendamentos.service.AgendamentoService;
import com.springbootessentials.sistemaagendamentos.service.PortalProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cliente")
@RequiredArgsConstructor
public class ClientPortalController {
    private final AgendamentoService agendamentoService;
    private final PortalProfileService profileService;

    @GetMapping("/agendamentos")
    public List<AppointmentResponse> appointments(@AuthenticationPrincipal PortalPrincipal principal) {
        return agendamentoService.listForClient(principal.id());
    }

    @PostMapping("/agendamentos")
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse book(@AuthenticationPrincipal PortalPrincipal principal,
                                    @Valid @RequestBody AppointmentBookingRequest request) {
        return agendamentoService.bookForClient(principal.id(), request);
    }

    @DeleteMapping("/agendamentos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@AuthenticationPrincipal PortalPrincipal principal, @PathVariable Integer id) {
        agendamentoService.cancelForClient(principal.id(), id);
    }

    @GetMapping("/perfil")
    public ClientProfileResponse profile(@AuthenticationPrincipal PortalPrincipal principal) {
        return profileService.getClientProfile(principal.id());
    }

    @PutMapping("/perfil")
    public ClientProfileResponse updateProfile(@AuthenticationPrincipal PortalPrincipal principal,
                                               @Valid @RequestBody ClientProfileUpdateRequest request) {
        return profileService.updateClientProfile(principal.id(), request);
    }
}
