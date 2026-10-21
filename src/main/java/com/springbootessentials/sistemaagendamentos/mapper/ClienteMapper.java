package com.springbootessentials.sistemaagendamentos.mapper;

import com.springbootessentials.sistemaagendamentos.domain.Cliente;
import com.springbootessentials.sistemaagendamentos.requests.clienteRequests.ClientePostRequest;
import com.springbootessentials.sistemaagendamentos.requests.clienteRequests.ClientePutRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class ClienteMapper {
    public static ClienteMapper INSTANCE = Mappers.getMapper(ClienteMapper.class);

    public abstract Cliente ToCliente(ClientePostRequest clientePostRequest);

    public abstract Cliente ToCliente(ClientePutRequest clientePutRequest);

}
