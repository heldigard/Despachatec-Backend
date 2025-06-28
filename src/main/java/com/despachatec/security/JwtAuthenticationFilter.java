package com.despachatec.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  private JwtTokenProvider tokenProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    // Obtener el token JWT de la solicitud HTTP
    String token = getJWTFromRequest(request);

    // Validar el token
    if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
      // Obtener el username del token
      String username = tokenProvider.getUsernameFromJWT(token);

      // Obtener los roles del token
      String roles = tokenProvider.getRolesFromJWT(token);

      // Convertir roles string a GrantedAuthorities
      Collection<GrantedAuthority> authorities = null;
      if (StringUtils.hasText(roles)) {
        authorities = Arrays.stream(roles.split(","))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
      }

      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
          username, null, authorities);
      authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      // Establecer la seguridad en el contexto
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    filterChain.doFilter(request, response);
  }

  // Método para obtener el token JWT del encabezado Authorization HTTP
  private String getJWTFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7); // Extraer el token excluyendo "Bearer "
    }
    return null;
  }
}
