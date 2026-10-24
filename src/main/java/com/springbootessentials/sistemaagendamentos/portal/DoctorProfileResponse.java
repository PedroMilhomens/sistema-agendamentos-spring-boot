package com.springbootessentials.sistemaagendamentos.portal;

import com.springbootessentials.sistemaagendamentos.domain.Doutor;

public record DoctorProfileResponse(Integer id, String name, String areaDeAtuacao, String email) {
    public static DoctorProfileResponse from(Doutor doctor) {
        return new DoctorProfileResponse(doctor.getId(), doctor.getName(), doctor.getAreaDeAtuacao(), doctor.getEmail());
    }
}
