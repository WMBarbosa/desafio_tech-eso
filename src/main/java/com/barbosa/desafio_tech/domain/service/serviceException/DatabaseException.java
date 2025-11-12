package com.barbosa.desafio_tech.domain.service.serviceException;

public class DatabaseException extends  RuntimeException {
    public DatabaseException(String message) {
        super(message);
    }
}
