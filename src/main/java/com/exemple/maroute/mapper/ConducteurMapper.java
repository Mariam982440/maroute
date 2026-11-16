
package com.exemple.maroute.mapper;

import com.exemple.maroute.dto.ConducteurRequestDTO;
import com.exemple.maroute.dto.ConducteurResponseDTO;
import com.exemple.maroute.entity.Conducteur;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConducteurMapper {

    Conducteur toEntity(ConducteurRequestDTO dto);

    ConducteurResponseDTO toResponseDTO(Conducteur conducteur);
}
