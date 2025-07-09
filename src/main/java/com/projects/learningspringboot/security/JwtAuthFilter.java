package com.projects.learningspringboot.security;

import com.projects.learningspringboot.model.authmodel.User;
import com.projects.learningspringboot.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();

        // Skip token validation for public routes
        if (path.equals("/api/v1/auth/login") || path.equals("/api/v1/auth/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            if (jwtUtil.validateToken(jwt)) {
                String username = jwtUtil.extractUsername(jwt);
                Optional<User> optionalUser = userRepository.findByUsername(username);
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();

//                    String role = mapRoleIdToRoleName(user.getRoleId());
                    String roleFromDb = mapRoleIdToRoleName(user.getRoleId());
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleFromDb);


//                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                    var auth = new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            null,
                            List.of(authority)
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }

        System.out.println("JWT Filter hit: " + request.getServletPath());


        filterChain.doFilter(request, response);
    }

    private String mapRoleIdToRoleName(Integer roleId) {
        return switch (roleId) {
            case 1 -> "ROLE_ADMIN";
            case 2 -> "ROLE_USER";
            default -> "ROLE_USER";
        };
    }
}
