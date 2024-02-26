package cl.camilo_poblete.ejercicio_tecnico.controller;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

// Clase para respuesta de la API

@Data
@AllArgsConstructor
@Schema(description = "Objeto que representa la respuesta de la API")
public class ApiResponseObject {

    @Schema(description = "resultado de la peticion",example="false")
    private Boolean success;

    @Schema(description = "mensaje de respuesta",example="peticion fallida")
    private String message;
}
