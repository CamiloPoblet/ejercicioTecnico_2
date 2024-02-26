package cl.camilo_poblete.ejercicio_tecnico.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "phones")
@Schema(description = "Objeto que representa un telefono asociado a un usuario")
public class Phone {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "number")
    @Schema(description = "Numero de telefono",example="71786225",requiredMode = Schema.RequiredMode.REQUIRED)
    private String number;

    @Column(name = "city_code")
    @Schema(description = "Codigo ciudad",example="9", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cityCode;

    @Column(name = "country_code")
    @Schema(description = "Codigo pais",example="56",requiredMode = Schema.RequiredMode.REQUIRED)
    private String countryCode;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonBackReference
    @Schema(description = "usuario asociado al telefono", hidden = true)
    private User user;

}