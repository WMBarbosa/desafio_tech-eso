package com.barbosa.desafio_tech.domain.service.serviceException;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(Object id) {
        super("Usuário com id: " + id + " não encontrado.");
    }
}
