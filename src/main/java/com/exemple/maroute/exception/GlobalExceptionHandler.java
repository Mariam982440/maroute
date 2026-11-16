package com.exemple.maroute.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 400 : validation Bean Validation (@Valid sur le DTO)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex,
                                          HttpServletRequest request) {
        Map<String, String> erreurs = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> erreurs.put(e.getField(), e.getDefaultMessage()));

        log.warn("Validation refusée sur {} : {}", request.getRequestURI(), erreurs);

        ProblemDetail pd = build(HttpStatus.BAD_REQUEST, "Erreur de validation",
                "Les données envoyées sont invalides", request);
        pd.setProperty("erreurs", erreurs);
        return pd;
    }

    // 400 : JSON mal formé, date illisible, enum inconnu...
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleUnreadable(HttpMessageNotReadableException ex,
                                          HttpServletRequest request) {
        log.warn("Corps de requête illisible sur {}", request.getRequestURI());
        return build(HttpStatus.BAD_REQUEST, "Requête invalide",
                "Le corps de la requête est mal formé ou contient une valeur invalide", request);
    }

    // 400 : paramètre d'URL du mauvais type (ex: /conducteurs/abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                            HttpServletRequest request) {
        log.warn("Paramètre invalide '{}' sur {}", ex.getName(), request.getRequestURI());
        return build(HttpStatus.BAD_REQUEST, "Paramètre invalide",
                "Le paramètre '" + ex.getName() + "' a une valeur invalide", request);
    }

    // 403
    @ExceptionHandler(ForbiddenException.class)
    public ProblemDetail handleForbidden(ForbiddenException ex, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, "Action interdite", ex.getMessage(), request);
    }

    // 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "Ressource introuvable", ex.getMessage(), request);
    }

    // 409
    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusiness(BusinessException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "Règle métier violée", ex.getMessage(), request);
    }

    // 500 : tout le reste. On ne montre pas le détail technique au client.
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Erreur interne sur {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne",
                "Une erreur interne est survenue", request);
    }

    private ProblemDetail build(HttpStatus status, String titre, String message,
                                HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, message);
        pd.setTitle(titre);
        pd.setProperty("timestamp", LocalDateTime.now());
        pd.setProperty("path", request.getRequestURI());
        return pd;
    }
}
