package com.elsherbieny.bookStore.security;

import com.elsherbieny.bookStore.model.Permission;
import com.elsherbieny.bookStore.model.User;
import com.elsherbieny.bookStore.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;

    public HeaderAuthenticationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String userId = request.getHeader("X-User-Id");

        if (userId != null && !userId.isBlank()) {
            authenticateUser(userId);
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(String userId) {
        try {
            userRepository.findById(Long.parseLong(userId))
                    .ifPresent(this::setAuthentication);
        } catch (NumberFormatException ignored) {
            SecurityContextHolder.clearContext();
        }
    }

    private void setAuthentication(User user) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        for (Permission permission : Permission.values()) {
            if (user.getRole().hasPermission(permission)) {
                authorities.add(new SimpleGrantedAuthority(permission.name()));
            }
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                null,
                authorities
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
