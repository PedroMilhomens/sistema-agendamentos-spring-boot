package com.springbootessentials.sistemaagendamentos.requests;

import lombok.Data;

@Data
public class DoutorPutRequest {
    private Integer id;
    private String name;
    private String areaDeAtuacao;
}
