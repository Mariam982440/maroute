package com.exemple.maroute.exception;

/**
 * Action sur la ressource d'un autre conducteur ou passager -> HTTP 403.
 * (Utile à partir du jour 2 : véhicule d'un autre conducteur, etc.)
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
