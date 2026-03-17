package com.avaneesh.notifcation_api.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    // Simulating a database of valid API keys.
    // In a massive production system, you would check this against Redis or PostgreSQL!
    private static final Map<String, String> VALID_API_KEYS = Map.of(
            "key_live_ecommerce123", "ecommerce-tenant",
            "key_live_hrapp456", "hr-tenant"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String apiKey = request.getHeader("X-API-KEY");

        if (apiKey == null || !VALID_API_KEYS.containsKey(apiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("401 Unauthorized: Invalid or missing X-API-KEY header");
            return;
        }

        String tenantId = VALID_API_KEYS.get(apiKey);

        request.setAttribute("tenantId", tenantId);

        filterChain.doFilter(request, response);
    }
}