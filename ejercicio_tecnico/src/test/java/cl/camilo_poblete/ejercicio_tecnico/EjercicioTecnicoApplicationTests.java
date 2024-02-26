package cl.camilo_poblete.ejercicio_tecnico;

import cl.camilo_poblete.ejercicio_tecnico.controller.ApiResponse;
import cl.camilo_poblete.ejercicio_tecnico.controller.UserController;
import cl.camilo_poblete.ejercicio_tecnico.dto.UserDto;
import cl.camilo_poblete.ejercicio_tecnico.entity.User;
import cl.camilo_poblete.ejercicio_tecnico.service.UserAlreadyExistsException;
import cl.camilo_poblete.ejercicio_tecnico.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest
class EjercicioTecnicoApplicationTests {

	@Value("${user.validation.password-regex}")
	String passwordRegex;
	@Value("${user.validation.email-regex}")
	String emailRegex;

	@Mock
	private UserService userService;

	@InjectMocks
	private UserController userController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		userController = new UserController(userService);
		// Configurar el controlador con las expresiones regulares de la configuración
		userController.setPasswordRegex(passwordRegex);
		userController.setEmailRegex(emailRegex);
	}

	@Test
	void registerUserSuccess() throws UserAlreadyExistsException {
		// Setup
		UserDto validUserDto = new UserDto();
		validUserDto.setEmail("test@example.com");
		validUserDto.setPassword("Password123");

		when(userService.createUser(any(UserDto.class))).thenReturn(new User());

		// Execution
		ResponseEntity<?> response = userController.registerUser(validUserDto);

		// Validation
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		verify(userService, times(1)).createUser(any(UserDto.class));
	}

	@Test
	void registerUserWithInvalidEmailFormat() throws UserAlreadyExistsException{
		// Setup
		UserDto invalidEmailUserDto = new UserDto();
		invalidEmailUserDto.setEmail("invalid-email");
		invalidEmailUserDto.setPassword("Password1");

		// Execution
		ResponseEntity<?> response = userController.registerUser(invalidEmailUserDto);

		// Validation
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void registerUserWithInvalidPasswordFormat() throws UserAlreadyExistsException{
		// Setup
		UserDto invalidPasswordUserDto = new UserDto();
		invalidPasswordUserDto.setEmail("test@example.com");
		invalidPasswordUserDto.setPassword("password");

		// Execution
		ResponseEntity<?> response = userController.registerUser(invalidPasswordUserDto);

		// Validation
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

	}

	@Test
	void registerUserWhenUserAlreadyExists() throws UserAlreadyExistsException {
		// Setup
		when(userService.createUser(any(UserDto.class))).thenThrow(new UserAlreadyExistsException("El correo ya está registrado"));

		UserDto userDto = new UserDto();
		userDto.setEmail("existing@example.com");
		userDto.setPassword("Password123");

		// Execution
		ResponseEntity<?> response = userController.registerUser(userDto);

		// Validation
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		verify(userService, times(1)).createUser(any(UserDto.class));
		ApiResponse apiResponse = (ApiResponse) response.getBody();
		assertNotNull(apiResponse);
		assertFalse(apiResponse.getSuccess());
		assertEquals("El correo ya está registrado", apiResponse.getMessage());
	}

}