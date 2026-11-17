package com.exemple.maroute.mapper;

import com.exemple.maroute.dto.VehiculeRequestDTO;
import com.exemple.maroute.dto.VehiculeResponseDTO;
import com.exemple.maroute.entity.Vehicule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VehiculeMapper {

    @Mapping(target = "conducteur", ignore = true)
    Vehicule toEntity(VehiculeRequestDTO dto);

    @Mapping(source = "conducteur.id", target = "conducteurId")
    VehiculeResponseDTO toResponseDTO(Vehicule vehicule);

    @Mapping(target = "conducteur", ignore = true)
    void updateEntity(VehiculeRequestDTO dto, @MappingTarget Vehicule vehicule);

}

