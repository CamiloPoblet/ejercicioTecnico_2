package cl.camilo_poblete.ejercicio_tecnico.controller;

import cl.camilo_poblete.ejercicio_tecnico.dto.UserDto;
import cl.camilo_poblete.ejercicio_tecnico.entity.User;
import cl.camilo_poblete.ejercicio_tecnico.service.UserAlreadyExistsException;
import cl.camilo_poblete.ejercicio_tecnico.service.UserService;
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

    @CrossOrigin
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        try {

            if(!(userDto.getPassword().matches(passwordRegex)))
            {
                logger.debug("La contraseña no cumple con el formato");
                return new ResponseEntity<>(new ApiResponse(false,"La contraseña no cumple con el formato"),HttpStatus.BAD_REQUEST);
            }

            if(!(userDto.getEmail().matches(emailRegex)))
            {
                logger.debug("el email no cumple con el formato");
                return new ResponseEntity<>(new ApiResponse(false,"El email no cumple con el formato"),HttpStatus.BAD_REQUEST);
            }

            User savedUser = userService.createUser(userDto);
            // Se omite devolver la contraseña por razones de seguridad
            savedUser.setPassword(null);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (UserAlreadyExistsException e) {
            logger.error(e.toString());
            return new ResponseEntity<>(new ApiResponse(false, "El correo ya está registrado"), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResponseEntity<>(new ApiResponse(false, "Error al registrar el usuario Exception"+e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @PostMapping("/auth")
    public ResponseEntity<?> authenticateUser(@RequestBody UserDto userDto) {
        try {
            User authenticatedUser = userService.authenticateUser(userDto.getEmail(), userDto.getPassword());
            String token = userService.generateJwtToken(authenticatedUser);
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (AuthenticationException e) {
            logger.error("Error de autenticación: " + e.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Autenticación fallida"), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("Error interno: " + e.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Error interno del servidor"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error al buscar el usuario: " + e.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Error al buscar el usuario"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
