package cl.camilo_poblete.ejercicio_tecnico.service;

public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
