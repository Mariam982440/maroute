package com.exemple.maroute.exception;

/**
 * Ressource inexistante (conducteur, véhicule, trajet...) -> HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}