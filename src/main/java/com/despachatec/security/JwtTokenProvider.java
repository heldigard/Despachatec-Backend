package com.despachatec.security;

import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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

    // Obtener roles del usuario autenticado
    String roles = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    return Jwts.builder()
        .setSubject(username)
        .claim("roles", roles)
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

  // Obtener roles del token JWT
  public String getRolesFromJWT(String token) {
    Claims claims = Jwts.parser()
        .setSigningKey(jwtSecret)
        .parseClaimsJws(token)
        .getBody();
    return claims.get("roles", String.class);
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
