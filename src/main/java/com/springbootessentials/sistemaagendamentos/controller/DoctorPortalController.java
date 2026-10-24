package com.springbootessentials.sistemaagendamentos.controller;

import com.springbootessentials.sistemaagendamentos.auth.PortalPrincipal;
import com.springbootessentials.sistemaagendamentos.portal.AppointmentResponse;
import com.springbootessentials.sistemaagendamentos.portal.DoctorProfileResponse;
import com.springbootessentials.sistemaagendamentos.portal.DoctorProfileUpdateRequest;
import com.springbootessentials.sistemaagendamentos.service.AgendamentoService;
import com.springbootessentials.sistemaagendamentos.service.PortalProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doutor")
@RequiredArgsConstructor
public class DoctorPortalController {
    private final AgendamentoService agendamentoService;
    private final PortalProfileService profileService;

    @GetMapping("/agendamentos")
    public List<AppointmentResponse> appointments(@AuthenticationPrincipal PortalPrincipal principal) {
        return agendamentoService.listForDoctor(principal.id());
    }

    @GetMapping("/perfil")
    public DoctorProfileResponse profile(@AuthenticationPrincipal PortalPrincipal principal) {
        return profileService.getDoctorProfile(principal.id());
    }

    @PutMapping("/perfil")
    public DoctorProfileResponse updateProfile(@AuthenticationPrincipal PortalPrincipal principal,
                                               @Valid @RequestBody DoctorProfileUpdateRequest request) {
        return profileService.updateDoctorProfile(principal.id(), request);
    }
}
