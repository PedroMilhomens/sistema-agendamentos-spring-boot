package com.springbootessentials.sistemaagendamentos.requests;

import lombok.Data;

@Data
public class AgendamentoPutRequest {
    private Integer id;
    private String nome;
    private String data;
    private String hora;
    private String descricao;
    private Integer doctorId;
}
