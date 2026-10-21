package com.springbootessentials.sistemaagendamentos.requests;

import com.springbootessentials.sistemaagendamentos.requests.agendamentoRequests.AgendamentoPostRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgendamentoRequestValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsValidAppointmentRequest() {
        AgendamentoPostRequest request = new AgendamentoPostRequest();
        request.setData(LocalDate.of(2026, 10, 21));
        request.setHora(LocalTime.of(9, 30));
        request.setDescricao("Consulta");
        request.setDoctorId(3);
        request.setClienteId(8);

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void rejectsMissingRequiredAppointmentFields() {
        AgendamentoPostRequest request = new AgendamentoPostRequest();

        assertFalse(validator.validate(request).isEmpty());
    }
}
