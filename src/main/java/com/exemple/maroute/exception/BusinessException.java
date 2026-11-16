package com.exemple.maroute.exception;

/**
 * Règle métier violée (email déjà utilisé, trajet complet, délai dépassé...) -> HTTP 409.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
