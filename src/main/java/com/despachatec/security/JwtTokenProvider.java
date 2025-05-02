package com.despachatec.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtTokenProvider {

  @Value("${app.jwt-secret}")
  private String jwtSecret;

  @Value("${app.jwt-expiration-milliseconds}")
  private int jwtExpirationInMs;

  // Generar token JWT
  public String generateToken(Authentication authentication) {
    String username = authentication.getName();
    Date currentDate = new Date();
    Date expireDate = new Date(currentDate.getTime() + jwtExpirationInMs);

    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(currentDate)
        .setExpiration(expireDate)
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }

  // Obtener username del token JWT
  public String getUsernameFromJWT(String token) {
    Claims claims = Jwts.parser()
        .setSigningKey(jwtSecret)
        .parseClaimsJws(token)
        .getBody();
    return claims.getSubject();
  }

  // Validar token JWT
  public boolean validateToken(String token) {
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
      return true;
    } catch (SignatureException ex) {
      // Firma JWT no válida
      return false;
    } catch (MalformedJwtException ex) {
      // Token JWT no válido
      return false;
    } catch (ExpiredJwtException ex) {
      // Token JWT expirado
      return false;
    } catch (UnsupportedJwtException ex) {
      // Token JWT no soportado
      return false;
    } catch (IllegalArgumentException ex) {
      // Claims JWT vacío
      return false;
    }
  }
}
