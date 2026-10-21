package com.springbootessentials.sistemaagendamentos.mapper;

import com.springbootessentials.sistemaagendamentos.domain.Cliente;
import com.springbootessentials.sistemaagendamentos.requests.clienteRequests.ClientePostRequest;
import com.springbootessentials.sistemaagendamentos.requests.clienteRequests.ClientePutRequest;
import com.springbootessentials.sistemaagendamentos.response.ClienteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class ClienteMapper {
    public static ClienteMapper INSTANCE = Mappers.getMapper(ClienteMapper.class);

    public abstract Cliente ToCliente(ClientePostRequest clientePostRequest);

    public abstract Cliente ToCliente(ClientePutRequest clientePutRequest);

    public abstract ClienteResponse ToClienteResponse(Cliente cliente);

    public abstract List<ClienteResponse> ToClienteResponses(List<Cliente> clientes);

}
