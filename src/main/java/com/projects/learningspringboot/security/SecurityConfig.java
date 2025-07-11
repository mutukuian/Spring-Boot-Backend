package com.projects.learningspringboot.security;

import com.projects.learningspringboot.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public SecurityConfig(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
////                .authorizeHttpRequests(auth -> auth
////                        .requestMatchers("/api/v1/auth/").permitAll() // Allow access to the main action-based auth POST
////                        .requestMatchers("/api/v1/auth/change-password").permitAll()
////                        .requestMatchers("/api/v1/auth/users/**").hasRole("ADMIN")
////                        .anyRequest().hasAnyRole("USER", "ADMIN")
////                )
////                .authorizeHttpRequests(auth -> auth
////                        .requestMatchers("/api/v1/auth/").permitAll()
////                        .anyRequest().authenticated()
////                )
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/change-password").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/").permitAll() // ✅ Allow frontend fetch
//                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/**").permitAll() // ✅ Allow fetch by ID
//                        .anyRequest().authenticated()
//                )
//
//
//                .addFilterBefore(new JwtAuthFilter(jwtUtil, userRepository), UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ✅ Allow GET for fetchAllUsers via query param
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/users/**").permitAll()

                        // ✅ Allow POST actions (register, login, etc.)
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/change-password").permitAll()

                        // All others require auth
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthFilter(jwtUtil, userRepository), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}

/*
private static String sha256Hash(String password) {    try {
    MessageDigest md = MessageDigest.getInstance("SHA-256");
          md.update(password.getBytes(StandardCharsets.UTF_8));
           byte[] hashedBytes = md.digest();
               StringBuilder sb = new StringBuilder();
                       for (byte b : hashedBytes)
                        {            sb.append(String.format("%02x", b));        }
                           return sb.toString();    } catch (NoSuchAlgorithmException e)
                           {        // Handle the exception (e.g., log it) or throw a RuntimeException
     throw new RuntimeException("Error hashing password", e);    }}
 */