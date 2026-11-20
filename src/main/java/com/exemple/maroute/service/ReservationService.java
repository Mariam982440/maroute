package com.exemple.maroute.service;

import com.exemple.maroute.dto.ReservationRequestDTO;
import com.exemple.maroute.dto.ReservationResponseDTO;

import java.util.List;

public interface ReservationService {

    ReservationResponseDTO reserver(ReservationRequestDTO request);

    void annulerReservation(Long reservationId, Long passagerId);

    List<ReservationResponseDTO> consulterReservationsPassager(Long passagerId);

    List<ReservationResponseDTO> consulterReservationsTrajet(Long trajetId, Long conducteurId);
}
