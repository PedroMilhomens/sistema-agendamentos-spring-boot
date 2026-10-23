package com.springbootessentials.sistemaagendamentos.mapper;

import com.springbootessentials.sistemaagendamentos.domain.Agendamento;
import com.springbootessentials.sistemaagendamentos.requests.agendamentoRequests.AgendamentoPostRequest;
import com.springbootessentials.sistemaagendamentos.requests.agendamentoRequests.AgendamentoPutRequest;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AgendamentoMapper {
    public static final AgendamentoMapper INSTANCE = Mappers.getMapper(AgendamentoMapper.class);

    @Mapping(target = "doutor", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    public abstract Agendamento ToAgendamento(AgendamentoPostRequest agendamentoPostRequest);

    @Mapping(target = "doutor", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    public abstract Agendamento ToAgendamento(AgendamentoPutRequest agendamentoPutRequest);

}
