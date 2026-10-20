package com.springbootessentials.sistemaagendamentos.mapper;

import com.springbootessentials.sistemaagendamentos.domain.Doutor;
import com.springbootessentials.sistemaagendamentos.requests.DoutorPostRequest;
import com.springbootessentials.sistemaagendamentos.requests.DoutorPutRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class DoutorMapper {
    public static DoutorMapper INSTANCE = Mappers.getMapper(DoutorMapper.class);

    public abstract Doutor ToDoutor(DoutorPostRequest doutorPostRequest);

    public abstract Doutor ToDoutor(DoutorPutRequest doutorPutRequest);
}
