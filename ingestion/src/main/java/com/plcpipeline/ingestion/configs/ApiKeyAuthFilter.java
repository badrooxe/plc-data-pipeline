package com.plcpipeline.ingestion.configs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final String apiKeyHeaderName;
    private final String validApiKey;
    // private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // private static final List<String> PUBLIC_PATHS = Arrays.asList(
    //     "/v1/api-docs/**",
    //     "/v3/api-docs/**",
    //     "/swagger-ui.html",
    //     "/swagger-ui/**",
    //     "/actuator/health",
    //     "/actuator/info"
    // );
    

    public ApiKeyAuthFilter(String validApiKey, String apiKeyHeaderName) {
        this.validApiKey = validApiKey;
        this.apiKeyHeaderName = apiKeyHeaderName;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // boolean isPublicPath = PUBLIC_PATHS.stream()
        //         .anyMatch(path -> pathMatcher.match(path, request.getRequestURI()));

        // if (isPublicPath) {
        //     filterChain.doFilter(request, response);
        //     return;
        // }
        
        // String apiKeyHeader = request.getHeader(apiKeyHeaderName);

        // Only enforce API key if the request is mapped to a protected endpoint
        if (request.getRequestURI().startsWith("/api/")) {
            String apiKeyHeader = request.getHeader(apiKeyHeaderName);

            if (validApiKey.equals(apiKeyHeader)) {
                Authentication authentication = new ApiKeyAuthenticationToken(apiKeyHeader);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized: Missing or invalid API key");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    static class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {
        private final String apiKey;

        public ApiKeyAuthenticationToken(String apiKey) {
            super(null);
            this.apiKey = apiKey;
            setAuthenticated(true);
        }

        @Override
        public Object getCredentials() {
            return apiKey;
        }

        @Override
        public Object getPrincipal() {
            return "apiKeyUser";
        }
    }
}
