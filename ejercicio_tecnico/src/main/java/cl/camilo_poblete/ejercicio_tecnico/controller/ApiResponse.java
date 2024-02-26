package cl.camilo_poblete.ejercicio_tecnico.controller;


import lombok.AllArgsConstructor;
import lombok.Data;

// Clase para respuesta de la API

@Data
@AllArgsConstructor
public class ApiResponse {
    //resultado de la peticion
    private Boolean success;
    //mensaje de respuesta
    private String message;
}
