package cl.bernardo.ohiggins.api_gateway.security;

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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${app.jwt.audience:3ac3ede1-1d05-42df-8248-3fbde4240a87}")
    private String expectedAudience;

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

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(issuerUri);

        OAuth2TokenValidator<Jwt> defaultValidator = JwtValidators.createDefaultWithIssuer(issuerUri);
        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(expectedAudience);
        OAuth2TokenValidator<Jwt> combined = new DelegatingOAuth2TokenValidator<>(defaultValidator, audienceValidator);

        jwtDecoder.setJwtValidator(combined);
        return jwtDecoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/usuarios/**").hasRole("ADMINISTRATIVO")
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