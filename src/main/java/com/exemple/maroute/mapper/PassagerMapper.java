package com.exemple.maroute.mapper;

import com.exemple.maroute.dto.PassagerRequestDTO;
import com.exemple.maroute.dto.PassagerResponseDTO;
import com.exemple.maroute.entity.Passager;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PassagerMapper {

    Passager toEntity(PassagerRequestDTO dto);

    PassagerResponseDTO toResponseDTO(Passager passager);

    void updateEntity(PassagerRequestDTO dto, @MappingTarget Passager passager);
}
