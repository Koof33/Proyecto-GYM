package com.gym.api_gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Filtro JWT para el API Gateway (versión MVC/servlet).
 *
 * Flujo:
 * 1. Si la ruta es pública (login/register), deja pasar sin validar.
 * 2. Si no hay token Bearer, responde 401 Unauthorized.
 * 3. Si el token es válido, extrae idUsuario y rol, los inyecta como
 *    headers X-User-Id y X-User-Roles, y reenvía la petición al ms destino.
 * 4. Si el token es inválido o expirado, responde 401 Unauthorized.
 */
@Component
@Order(1)
public class JwtAuthFilter implements Filter {

    @Value("${jwt.secret}")
    private String secret;

    // Rutas que NO requieren token JWT
    private static final List<String> PUBLIC_PATHS = List.of(
            "/autenticacion/login",
            "/autenticacion/register"
    );

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getRequestURI();

        // ── Rutas públicas: pasan sin validar ──────────────────────────────
        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            chain.doFilter(request, response);
            return;
        }

        // ── Verificar que venga el header Authorization ────────────────────
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token requerido. Incluye Authorization: Bearer <token>\"}");
            return;
        }

        // ── Validar el token y extraer claims ──────────────────────────────
        try {
            String token = authHeader.substring(7);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Extraer datos del usuario desde el token
            String idUsuario = String.valueOf(claims.get("idUsuario"));
            String rol       = String.valueOf(claims.get("rol"));

            System.out.println(">>> Gateway JWT OK | idUsuario=" + idUsuario + " | rol=" + rol + " | path=" + path);

            // Inyectar los headers en el request que se reenvía al microservicio
            MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);
            mutableRequest.putHeader("X-User-Id",    idUsuario);
            mutableRequest.putHeader("X-User-Roles", rol);

            chain.doFilter(mutableRequest, response);

        } catch (Exception e) {
            System.out.println(">>> Gateway JWT INVÁLIDO: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token inválido o expirado\"}");
        }
    }
}
