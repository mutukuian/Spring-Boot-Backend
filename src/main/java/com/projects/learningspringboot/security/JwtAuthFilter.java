package com.projects.learningspringboot.security;

import com.projects.learningspringboot.model.User;
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

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            if (jwtUtil.validateToken(jwt)) {
                String username = jwtUtil.extractUsername(jwt); // 👈 use username
                Optional<User> optionalUser = userRepository.findByUsername(username); // 👈 use username
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
                    var auth = new UsernamePasswordAuthenticationToken(user.getUsername(), null, List.of(authority));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }

        }


        filterChain.doFilter(request, response);
    }
}
