package cl.camilo_poblete.ejercicio_tecnico.config;


import cl.camilo_poblete.ejercicio_tecnico.security.JwtTokenFilter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


// Configuracion de seguridad

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AntPathRequestMatcher aPRMRegister = new AntPathRequestMatcher("/api/users/register","POST");
        AntPathRequestMatcher aPRMAuth = new AntPathRequestMatcher("/api/users/auth","POST");
        AntPathRequestMatcher aPRMSwagger1 = new AntPathRequestMatcher("/v3/api-docs/**");
        AntPathRequestMatcher aPRMSwagger2 = new AntPathRequestMatcher("/swagger-ui/**");
        AntPathRequestMatcher aPRMSwagger4 = new AntPathRequestMatcher("/webjars/**");
        AntPathRequestMatcher aPRMSwagger5 = new AntPathRequestMatcher("/**","GET");



        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) //disable
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(new JwtTokenFilter(jwtConfig()), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((autz) ->
                        autz
                            .requestMatchers(HttpMethod.OPTIONS,"/api/**").permitAll()
                            .requestMatchers(aPRMRegister, aPRMAuth,aPRMSwagger1,aPRMSwagger2,aPRMSwagger4).permitAll()
                            .anyRequest().authenticated()).sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }
    @Bean
    public JwtConfig jwtConfig() {
        return new JwtConfig();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:8080"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        // se permiten todos los metodos y header para la ruta http://localhost:8080


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }


}
