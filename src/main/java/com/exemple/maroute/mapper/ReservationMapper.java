package com.exemple.maroute.mapper;

import com.exemple.maroute.dto.ReservationResponseDTO;
import com.exemple.maroute.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(source = "passager.id", target = "passagerId")
    @Mapping(source = "trajet.id", target = "trajetId")
    @Mapping(source = "pointMontee.id", target = "pointMonteeId")
    @Mapping(source = "pointMontee.ville", target = "villeMontee")
    @Mapping(source = "pointDescente.id", target = "pointDescenteId")
    @Mapping(source = "pointDescente.ville", target = "villeDescente")
    ReservationResponseDTO toResponseDTO(Reservation reservation);
}