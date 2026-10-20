package com.springbootessentials.sistemaagendamentos.requests;

import lombok.Data;

@Data
public class DoutorPostRequest {
    private Integer id;
    private String name;
    private String areaDeAtuacao;
}
