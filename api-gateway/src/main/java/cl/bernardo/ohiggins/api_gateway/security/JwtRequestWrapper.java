package cl.bernardo.ohiggins.api_gateway.security;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class JwtRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String> headersExtra = new HashMap<>();

    public JwtRequestWrapper(HttpServletRequest request, String email, String rol) {
        super(request);
        headersExtra.put("X-User-Email", email);
        headersExtra.put("X-User-Rol", rol);
    }

    @Override
    public String getHeader(String name) {
        String headerExtra = headersExtra.get(name);
        if (headerExtra != null) {
            return headerExtra;
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        java.util.List<String> names = Collections.list(super.getHeaderNames());
        names.addAll(headersExtra.keySet());
        return Collections.enumeration(names);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        String headerExtra = headersExtra.get(name);
        if (headerExtra != null) {
            return Collections.enumeration(Collections.singletonList(headerExtra));
        }
        return super.getHeaders(name);
    }
}