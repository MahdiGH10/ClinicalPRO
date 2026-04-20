package com.clinicpro.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, String id) {
        super(resourceName + " introuvable avec l'identifiant: " + id);
    }
}
