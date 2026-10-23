package com.springbootessentials.sistemaagendamentos.controller;

import com.springbootessentials.sistemaagendamentos.domain.Agendamento;
import com.springbootessentials.sistemaagendamentos.service.AgendamentoService;
import com.springbootessentials.sistemaagendamentos.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AgendamentoControllerTest {
    private final AgendamentoService service = mock(AgendamentoService.class);
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
            new AgendamentoController(new DateUtil(), service)).build();

    @Test
    void getsAppointmentByConventionalResourcePath() throws Exception {
        when(service.findById(7)).thenReturn(Agendamento.builder().id(7).build());

        mockMvc.perform(get("/api/agendamentos/7"))
                .andExpect(status().isOk());

        verify(service).findById(7);
    }

    @Test
    void rejectsInvalidAppointmentDate() throws Exception {
        mockMvc.perform(post("/api/agendamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"data":"not-a-date","hora":"09:30:00","descricao":"Consulta",
                                "doctorId":3,"clienteId":8}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsAppointmentWithMissingRequiredFields() throws Exception {
        mockMvc.perform(post("/api/agendamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatesAppointmentUsingIdInResourcePath() throws Exception {
        mockMvc.perform(put("/api/agendamentos/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"data":"2026-10-21","hora":"09:30:00","descricao":"Consulta",
                                "doctorId":3,"clienteId":8}
                                """))
                .andExpect(status().isNoContent());

        verify(service).update(eq(7), any());
    }
}
