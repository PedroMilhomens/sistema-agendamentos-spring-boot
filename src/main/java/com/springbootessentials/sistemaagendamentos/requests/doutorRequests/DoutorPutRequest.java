package com.springbootessentials.sistemaagendamentos.requests.doutorRequests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DoutorPutRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String areaDeAtuacao;
}
