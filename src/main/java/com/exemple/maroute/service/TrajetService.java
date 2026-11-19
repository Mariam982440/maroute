package com.exemple.maroute.service;

import com.exemple.maroute.dto.TrajetRequestDTO;
import com.exemple.maroute.dto.TrajetResponseDTO;

public interface TrajetService {

    TrajetResponseDTO publierTrajet(TrajetRequestDTO request);

    TrajetResponseDTO consulterTrajet(Long id);
}
