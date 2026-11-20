package com.exemple.maroute.controller;


import com.exemple.maroute.entity.Trajet;
import com.exemple.maroute.service.TrajetService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/trajets")
public class TrajetController {

    private final TrajetService trajetService ;

    public TrajetController(TrajetService trajetService){
        this.trajetService = trajetService;
    }


}
