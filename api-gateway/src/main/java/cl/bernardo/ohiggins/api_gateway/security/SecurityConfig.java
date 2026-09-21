package cl.bernardo.ohiggins.api_gateway.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${app.jwt.audience}")
    private String expectedAudience;

    @Value("${app.cors.allowed-origins}")
    private List<String> allowedOrigins;

    private final CustomJwtAuthenticationConverter customJwtAuthenticationConverter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(CustomJwtAuthenticationConverter customJwtAuthenticationConverter,
                          CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                          CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.customJwtAuthenticationConverter = customJwtAuthenticationConverter;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    // Valida firma, emisor y vigencia con el validador por defecto, y le suma
    // la comprobacion de audiencia: que el token venga dirigido a esta API.
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(issuerUri);
        OAuth2TokenValidator<Jwt> defaultValidator = JwtValidators.createDefaultWithIssuer(issuerUri);
        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(expectedAudience);
        jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(defaultValidator, audienceValidator));
        return jwtDecoder;
    }

    // Spring Security tiene que conocer la configuracion de CORS. Con un filtro
    // suelto no basta: corre despues y la peticion previa del navegador, que va
    // sin token, ya fue rechazada con 401.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
                        .authorizeHttpRequests(auth -> auth
                // Cualquier usuario autenticado necesita poder resolver su propia
                // identidad interna. Va ANTES de la regla general de /api/usuarios:
                // en Spring Security gana la primera coincidencia.
                        .requestMatchers("/api/usuarios/me", "/api/usuarios/directorio").authenticated()
                        .requestMatchers("/api/usuarios/**").hasRole("ADMINISTRATIVO")

                // Un apoderado puede consultar las matriculas de sus pupilos.
                    .requestMatchers("/api/matricula/apoderado/**").hasAnyRole("ADMINISTRATIVO", "APODERADO")
                    .requestMatchers("/api/matricula/**").hasRole("ADMINISTRATIVO")

                    .requestMatchers("/api/academico/**").hasAnyRole("ADMINISTRATIVO", "PROFESOR")
                    .requestMatchers("/api/reportes/**").hasAnyRole("ADMINISTRATIVO", "PROFESOR")
                    .requestMatchers("/api/libro/**").hasAnyRole("ADMINISTRATIVO", "PROFESOR", "ESTUDIANTE", "APODERADO")
                    .requestMatchers("/api/comunicaciones/**").authenticated()
                    .anyRequest().authenticated()
                )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                .decoder(jwtDecoder())
                .jwtAuthenticationConverter(customJwtAuthenticationConverter)
            ));

        return http.build();
    }
}