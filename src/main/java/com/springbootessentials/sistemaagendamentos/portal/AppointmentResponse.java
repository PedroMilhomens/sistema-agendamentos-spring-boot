package com.springbootessentials.sistemaagendamentos.portal;

import com.springbootessentials.sistemaagendamentos.domain.Agendamento;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentResponse(
        Integer id,
        LocalDate data,
        LocalTime hora,
        String descricao,
        Integer doctorId,
        String doctorName,
        Integer clienteId,
        String clientName
) {
    public static AppointmentResponse from(Agendamento appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getData(),
                appointment.getHora(),
                appointment.getDescricao(),
                appointment.getDoutor().getId(),
                appointment.getDoutor().getName(),
                appointment.getCliente().getId(),
                appointment.getCliente().getName()
        );
    }
}
