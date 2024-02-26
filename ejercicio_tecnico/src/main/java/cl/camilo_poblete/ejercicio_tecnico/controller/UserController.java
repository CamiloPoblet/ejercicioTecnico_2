package cl.camilo_poblete.ejercicio_tecnico.controller;

import cl.camilo_poblete.ejercicio_tecnico.dto.UserDto;
import cl.camilo_poblete.ejercicio_tecnico.entity.User;
import cl.camilo_poblete.ejercicio_tecnico.service.UserAlreadyExistsException;
import cl.camilo_poblete.ejercicio_tecnico.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(name = "UserController", description = "Controller for user operations.")
@Getter
@Setter
@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${user.validation.password-regex}")
    private String passwordRegex;
    @Value("${user.validation.email-regex}")
    private String emailRegex;
    private final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @Operation(summary = "Registra a un nuevo usuario en la aplicacion",
            description = "Registra un nuevo usuario en la aplicacion y obtiene un token para operar con la api",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario creado con exito.",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class))}),
                    @ApiResponse(responseCode = "400", description = "La contraseña no cumple con el formato deseado",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseObject.class))}),
                    @ApiResponse(responseCode = "400", description = "El email no cumple con el formato deseado",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseObject.class))}),
                    @ApiResponse(responseCode = "409", description = "El correo ya esta registrado.",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseObject.class))}),
                    @ApiResponse(responseCode = "500", description = "Error al registrar al usuario.",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseObject.class))})
            })
    @CrossOrigin
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        try {

            if(!(userDto.getPassword().matches(passwordRegex)))
            {
                logger.debug("La contraseña no cumple con el formato");
                return new ResponseEntity<>(new ApiResponseObject(false,"La contraseña no cumple con el formato"),HttpStatus.BAD_REQUEST);
            }

            if(!(userDto.getEmail().matches(emailRegex)))
            {
                logger.debug("el email no cumple con el formato");
                return new ResponseEntity<>(new ApiResponseObject(false,"El email no cumple con el formato"),HttpStatus.BAD_REQUEST);
            }

            User savedUser = userService.createUser(userDto);
            // Se omite devolver la contraseña por razones de seguridad
            savedUser.setPassword(null);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (UserAlreadyExistsException e) {
            logger.error(e.toString());
            return new ResponseEntity<>(new ApiResponseObject(false, "El correo ya está registrado"), HttpStatus.CONFLICT);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResponseEntity<>(new ApiResponseObject(false, "Error al registrar el usuario Exception"+e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Autentica a un usuario con su email y contraseña con el que fue creado",
            description = "Endpoint para autenticar a un usuario y obtener un token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario autenticado con exito.",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class))}),
                    @ApiResponse(responseCode = "401", description = "Autenticacion fallida del usuario.",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseObject.class))}),
                    @ApiResponse(responseCode = "500", description = "Error interno al autenticar el usuario.",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseObject.class))})
            })
    @CrossOrigin
    @PostMapping("/auth")
    public ResponseEntity<?> authenticateUser(@RequestBody UserDto userDto) {
        try {
            User authenticatedUser = userService.authenticateUser(userDto.getEmail(), userDto.getPassword());
            String token = userService.generateJwtToken(authenticatedUser);
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (AuthenticationException e) {
            logger.error("Error de autenticación: " + e.getMessage());
            return new ResponseEntity<>(new ApiResponseObject(false, "Autenticación fallida"), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("Error interno: " + e.getMessage());
            return new ResponseEntity<>(new ApiResponseObject(false, "Error interno del servidor"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Encuentra a un usuario por mail",
            description = "Endpoint para encontrar a un usuario por su email. Requiere autenticacion por token.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario Encontrado.",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class))}),
                    @ApiResponse(responseCode = "404", description = "Usuario NO Encontrado.",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseObject.class))}),
                    @ApiResponse(responseCode = "500", description = "Error interno al buscar al usuario.",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseObject.class))})
            })
    @GetMapping("/search")
    public ResponseEntity<?> findUserByEmail(@RequestParam("email") String email) {
        try {
            User user = userService.findUserByEmail(email);
            if (user != null) {
                //se omiten contrasenna y token
                user.setPassword(null);
                user.setToken(null);
                return ResponseEntity.ok(user);
            } else {
                return new ResponseEntity<>(new ApiResponseObject(false, "usuario no encontrado"), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error al buscar el usuario: " + e.getMessage());
            return new ResponseEntity<>(new ApiResponseObject(false, "Error al buscar el usuario"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
