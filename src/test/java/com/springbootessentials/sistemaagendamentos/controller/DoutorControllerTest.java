package com.springbootessentials.sistemaagendamentos.controller;

import com.springbootessentials.sistemaagendamentos.service.DoutorService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class DoutorControllerTest {
    private final DoutorService service = mock(DoutorService.class);
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new DoutorController(service)).build();

    @Test
    void deletesDoctorByPathIdWithoutRequestBody() throws Exception {
        mockMvc.perform(delete("/api/doutores/7"))
                .andExpect(status().isNoContent());

        verify(service).delete(7);
    }
}
