package com.gym.api_gateway.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.*;

/**
 * Wrapper de HttpServletRequest que permite agregar headers personalizados.
 * El gateway MVC no permite modificar headers directamente en el request
 * original (es inmutable), así que se usa este wrapper para inyectar
 * X-User-Id y X-User-Roles antes de reenviar la petición al microservicio.
 */
public class MutableHttpServletRequest extends HttpServletRequestWrapper {

    private final Map<String, String> customHeaders = new HashMap<>();

    public MutableHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    public void putHeader(String name, String value) {
        customHeaders.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        // Si el header está en los personalizados, lo devuelve primero
        String value = customHeaders.get(name);
        return value != null ? value : super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (customHeaders.containsKey(name)) {
            return Collections.enumeration(List.of(customHeaders.get(name)));
        }
        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> names = new HashSet<>(customHeaders.keySet());
        Enumeration<String> original = super.getHeaderNames();
        while (original.hasMoreElements()) {
            names.add(original.nextElement());
        }
        return Collections.enumeration(names);
    }
}
