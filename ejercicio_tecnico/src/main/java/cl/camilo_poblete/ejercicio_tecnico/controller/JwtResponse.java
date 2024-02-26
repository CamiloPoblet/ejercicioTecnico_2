package cl.camilo_poblete.ejercicio_tecnico.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Objeto que representa la respuesta de la API para la entrega del token al momento de autenticar un usuario")
@Data
public class JwtResponse {
    @Schema(description = "Token autenticador")
    private String token;
    @Schema(description = "Tipo de token",example = "Bearer")
    private String type = "Bearer";

    public JwtResponse(String token) {
        this.token = token;
    }

}