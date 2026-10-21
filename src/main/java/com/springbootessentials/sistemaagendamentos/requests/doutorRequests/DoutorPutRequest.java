package com.springbootessentials.sistemaagendamentos.requests.doutorRequests;

import lombok.Data;

@Data
public class DoutorPutRequest {
    private Integer id;
    private String name;
    private String areaDeAtuacao;
}
