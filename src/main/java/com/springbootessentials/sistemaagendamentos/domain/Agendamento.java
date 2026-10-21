package com.springbootessentials.sistemaagendamentos.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Agendamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDate data;
    private LocalTime hora;
    private String descricao;
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "idDoutor", nullable = false)
    private Doutor doutor;
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "idCliente", nullable = false)
    private Cliente cliente;

    @JsonProperty("idDoutor")
    public Integer getIdDoutor() {
        return doutor == null ? null : doutor.getId();
    }

    @JsonProperty("idCliente")
    public Integer getIdCliente() {
        return cliente == null ? null : cliente.getId();
    }
}
