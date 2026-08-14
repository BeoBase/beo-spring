package com.beobase.beospring.config;

import com.beobase.beospring.shared.TokenService;
import com.beobase.beospring.user.UserInfo;
import com.beobase.beospring.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(TokenAuthenticationFilter.class);

    private final TokenService tokenService;
    private final UserService userService;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String userId = tokenService.extractUserId(token);
            if (userId != null) {
                UserInfo userInfo = userService.findById(userId);
                if (userInfo != null) {
                    String role = userInfo.role();
                    var authority = new SimpleGrantedAuthority(role);
                    var authentication = new UsernamePasswordAuthenticationToken(
                            userInfo.id(),
                            null,
                            List.of(authority)
                    );
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);

                    logger.debug(
                            "Authenticated user {} with role {}",
                            userInfo.id(),
                            role
                    );
                }
            }
        } catch (Exception e) {
            logger.warn("Token authentication failed: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
}
