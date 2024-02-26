package cl.camilo_poblete.ejercicio_tecnico.dto;

import cl.camilo_poblete.ejercicio_tecnico.entity.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Objeto de transferencia que representa un usuario")
public class UserDto {

    @NotEmpty(message = "El nombre no puede estar vacío")
    @Schema(description = "nombre del usuario",example="Luis Pereira")
    private String name;

    @Schema(description = "email del usuario",example="luisPereira@domino.cl")
    @NotEmpty(message = "El correo no puede estar vacío")
    private String email;

    @NotEmpty(message = "La contraseña no puede estar vacía")
    @Schema(description = "contraseña del usuario",example="pasSwoRd123")
    private String password;
    @Schema(description = "Lista de telefonos asociada al usuario")
    private List<Phone> phones;
}