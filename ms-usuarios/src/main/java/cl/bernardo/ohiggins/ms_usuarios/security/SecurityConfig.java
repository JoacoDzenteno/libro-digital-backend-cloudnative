package cl.bernardo.ohiggins.ms_usuarios.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS se configura solo en el api-gateway, que es el unico componente que
    // recibe peticiones del navegador. Si tambien se configura aca, la respuesta
    // sale con el encabezado Access-Control-Allow-Origin duplicado y el
    // navegador la rechaza.
    //
    // Este servicio queda abierto a proposito: la autorizacion por rol vive en
    // el gateway, que es la frontera de seguridad. Se deja el resource server
    // para poder leer los claims del token, por ejemplo el oid en /me.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}));

        return http.build();
    }
}