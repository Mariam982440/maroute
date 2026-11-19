package com.exemple.maroute.mapper;

import com.exemple.maroute.dto.PointArretResponseDTO;
import com.exemple.maroute.dto.TrajetResponseDTO;
import com.exemple.maroute.entity.PointArret;
import com.exemple.maroute.entity.Trajet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ConducteurMapper.class, VehiculeMapper.class})
public interface TrajetMapper {

    @Mapping(target = "id", source = "trajet.id")
    @Mapping(target = "conducteur", source = "trajet.conducteur")
    @Mapping(target = "vehicule", source = "trajet.vehicule")
    @Mapping(target = "villeDepart", source = "trajet.villeDepart")
    @Mapping(target = "villeArrivee", source = "trajet.villeArrivee")
    @Mapping(target = "dateDepart", source = "trajet.dateDepart")
    @Mapping(target = "placesProposees", source = "trajet.placesProposees")
    @Mapping(target = "placesRestantes", source = "placesRestantes")
    @Mapping(target = "statut", source = "trajet.statut")
    @Mapping(target = "pointsArret", source = "trajet.pointsArret")
    TrajetResponseDTO toResponseDTO(Trajet trajet, int placesRestantes);

    PointArretResponseDTO toPointArretResponseDTO(PointArret pointArret);
}
