
package com.exemple.maroute.mapper;

import com.exemple.maroute.dto.ConducteurRequestDTO;
import com.exemple.maroute.dto.ConducteurResponseDTO;
import com.exemple.maroute.entity.Conducteur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ConducteurMapper {

    @Mapping(target = "vehicules", ignore = true)
    Conducteur toEntity(ConducteurRequestDTO dto);

    ConducteurResponseDTO toResponseDTO(Conducteur conducteur);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicules", ignore = true)
    void updateEntity(ConducteurRequestDTO request,
                      @MappingTarget Conducteur conducteur);
}
