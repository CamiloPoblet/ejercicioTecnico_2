package cl.camilo_poblete.ejercicio_tecnico.service;

import cl.camilo_poblete.ejercicio_tecnico.config.JwtConfig;
import cl.camilo_poblete.ejercicio_tecnico.dto.UserDto;
import cl.camilo_poblete.ejercicio_tecnico.entity.Phone;
import cl.camilo_poblete.ejercicio_tecnico.entity.User;
import cl.camilo_poblete.ejercicio_tecnico.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,JwtConfig jwtConfig) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtConfig= jwtConfig;
    }

    @Transactional
    public User createUser(UserDto userDto) throws UserAlreadyExistsException {
        logger.debug("UserService createUser userDto:"+userDto);
        try{
        validateEmailUnique(userDto.getEmail());

        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setCreated(LocalDateTime.now());
        user.setModified(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setActive(true);

        User savedUser= userRepository.save(user);

        String jwToken= generateJwtToken(savedUser);
        user.setToken(jwToken);

        List<Phone> phonesFromDto= userDto.getPhones();
        for (Phone phone: phonesFromDto)
        {
            if(user.getPhones()==null){
                user.setPhones(new ArrayList<Phone>());
            }
            user.getPhones().add(phone);
            phone.setUser(user);
        }
        return userRepository.save(user);
        }catch(Exception e)
        {
            logger.error(e.toString());
            throw e;
        }
    }

    private void validateEmailUnique(String email) throws UserAlreadyExistsException {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("El correo ya registrado");
        }
    }

    public User authenticateUser(String email, String rawPassword) throws AuthenticationException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el correo: " + email));

        if (passwordEncoder.matches(rawPassword, user.getPassword())) {
            return user;
        } else {
            throw new BadCredentialsException("Autenticación fallida");
        }
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public String generateJwtToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
            return JWT.create()
                    .withSubject(user.getId().toString())
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
                    .withClaim("email", user.getEmail())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error al generar el token JWT");
        }
    }
}