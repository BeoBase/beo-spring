package com.beobase.beospring.config;

import com.beobase.beospring.shared.TokenService;
import com.beobase.beospring.user.UserInfo;
import com.beobase.beospring.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenAuthenticationFilterTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private TokenAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new TokenAuthenticationFilter(tokenService, userService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueFilterChainWhenAuthorizationHeaderIsMissing()
            throws ServletException, IOException {

        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(tokenService, userService);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldContinueFilterChainWhenAuthorizationHeaderIsNotBearer()
            throws ServletException, IOException {

        when(request.getHeader("Authorization"))
                .thenReturn("Basic abc123");

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(tokenService, userService);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldAuthenticateUserWhenTokenIsValid()
            throws ServletException, IOException {

        String token = "valid-token";
        String userId = "user-123";
        String role = "ROLE_USER";

        UserInfo userInfo = new UserInfo(
                userId,
                "user name",
                "user@example.com",
                role
        );

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);
        when(tokenService.extractUserId(token))
                .thenReturn(userId);
        when(userService.findById(userId))
                .thenReturn(userInfo);

        filter.doFilter(request, response, filterChain);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals(userId, authentication.getPrincipal());
        assertTrue(authentication.isAuthenticated());
        assertEquals(1, authentication.getAuthorities().size());
        assertEquals(
                role,
                authentication.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
        );

        verify(tokenService).extractUserId(token);
        verify(userService).findById(userId);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenTokenServiceReturnsNullUserId()
            throws ServletException, IOException {

        String token = "invalid-token";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);
        when(tokenService.extractUserId(token))
                .thenReturn(null);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(tokenService).extractUserId(token);
        verifyNoInteractions(userService);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenUserIsNotFound()
            throws ServletException, IOException {

        String token = "valid-token";
        String userId = "missing-user";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);
        when(tokenService.extractUserId(token))
                .thenReturn(userId);
        when(userService.findById(userId))
                .thenReturn(null);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(tokenService).extractUserId(token);
        verify(userService).findById(userId);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldClearSecurityContextWhenTokenAuthenticationFails()
            throws ServletException, IOException {

        String token = "invalid-token";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);
        when(tokenService.extractUserId(token))
                .thenThrow(new RuntimeException("Invalid token"));

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(tokenService).extractUserId(token);
        verifyNoInteractions(userService);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldContinueFilterChainAfterSuccessfulAuthentication()
            throws ServletException, IOException {

        String token = "valid-token";
        String userId = "user-123";

        UserInfo userInfo = new UserInfo(
                userId,
                "user name",
                "user@example.com",
                "ROLE_USER"
        );

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);
        when(tokenService.extractUserId(token))
                .thenReturn(userId);
        when(userService.findById(userId))
                .thenReturn(userInfo);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

}
