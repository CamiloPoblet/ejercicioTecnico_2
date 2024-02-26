package cl.camilo_poblete.ejercicio_tecnico.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@ToString
@Table(name = "users")
@Schema(description = "Usuario de la aplicacion")
public class User {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;


    @NotBlank(message = "Name is required")
    @Schema(description = "Nombre del usuario",example="Luis Pereira",requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Column(unique = true)
    @Schema(description = "Email del usuario",example="luisPereira@dominio.cl",requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(description = "contraseña del usuario",example="contraSenna12",requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    @Schema(description = "Lista de telefonos asociados al usuario")
    private List<Phone> phones;

    @Schema(description = "fecha de creacion del usuario")
    private LocalDateTime created;
    @Schema(description = "fecha de modificacion del usuario")
    private LocalDateTime modified;
    @Schema(description = "fecha del ultimo login del usuario")
    private LocalDateTime lastLogin;
    @Schema(description = "token JWT del usuario")
    private String token;
    @Schema(description = "estado de actividad del usuario")
    private boolean isActive;


}