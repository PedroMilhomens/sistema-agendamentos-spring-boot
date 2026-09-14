package com.springbootessentials.sistemaagendamentos.mapper;

import com.springbootessentials.sistemaagendamentos.domain.Agendamento;
import com.springbootessentials.sistemaagendamentos.requests.AgendamentoPostRequest;
import com.springbootessentials.sistemaagendamentos.requests.AgendamentoPutRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public abstract class AgendamentoMapper {
    public static final AgendamentoMapper INSTANCE = Mappers.getMapper(AgendamentoMapper.class);

    public abstract Agendamento ToAgendamento(AgendamentoPostRequest agendamentoPostRequest);

    public abstract Agendamento ToAgendamento(AgendamentoPutRequest agendamentoPutRequest);

}
