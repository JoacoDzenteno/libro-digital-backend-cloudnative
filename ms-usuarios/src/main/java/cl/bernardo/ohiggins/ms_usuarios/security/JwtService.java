package cl.bernardo.ohiggins.ms_usuarios.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${app.jwt.secret}") 
    private String secretKey;
//iyecta valor desde el application.yml. Es la llave sercreta que se usa para firmar y validar el token
    @Value("${app.jwt.expiration}")
    private long jwtExpiration;
//Inyecta el tiempo de expiración del token en milisegundos (86400000 = 24 horas)
    public String generateToken(String email, String rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", rol);
//Crea un mapa de claims (datos adicionales que viajan dentro del token). Aquí agrega el rol del usuario.
        return Jwts.builder()
                .setClaims(claims)
                //Inicia la construcción del token y establece los claims personalizados (el rol).
                .setSubject(email)
                //Establece el "sujeto" del token, que es el email del usuario. Es el identificador principal.
                .setIssuedAt(new Date())
                //Registra la fecha y hora exacta en que se generó el token.
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                //Calcula la fecha de expiración sumando el tiempo actual + 24 horas.
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                //Firma el token usando la llave secreta y el algoritmo HMAC-SHA256, garantizando que nadie pueda modificarlo.
                .compact();
                //Construye y retorna el token como string en formato xxxxx.yyyyy.zzzzz
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
//Recibe el token, lo verifica con la llave secreta y extrae el email (subject) que está dentro del token.

    public String extractRol(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("rol");
    }
//Recibe el token, lo verifica con la llave secreta y extrae el rol que está dentro del token.
    
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
/*Verifica si el token es válido. Si el token es correcto y no ha expirado, retorna true. 
Si el token es inválido o ha expirado, captura la excepción y retorna false */    

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
/*Decodifica la llave secreta desde Base64 a bytes y la convierte en un objeto Key que JJWT 
puede usar para firmar y verificar tokens. Es private porque solo la usan los métodos internos 
de esta clase. */
}
