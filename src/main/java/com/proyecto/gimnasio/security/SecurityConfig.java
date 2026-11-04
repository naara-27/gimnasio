
package com.proyecto.gimnasio.security;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        @Value("${app.security.enabled:true}")
        private boolean securityEnabled;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws ExecutionException {

                if (!securityEnabled) {
                        http
                                        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                                        .csrf(csrf -> csrf.disable())
                                        .headers(h -> h.frameOptions(f -> f.sameOrigin()));

                        return http.build();
                }

                http
                                .authorizeHttpRequests(auth -> auth

                                                // Acceso público
                                                .requestMatchers("/login", "/h2-console/**", "/recuperar-password",
                                                                "/usuarios/recuperar-password", "/reset-password",
                                                                "/usuarios/reset-password")
                                                .permitAll()

                                                // Reservaciones: administrador
                                                .requestMatchers("/reservaciones").hasRole("ADMIN")

                                                .requestMatchers("/reservaciones/*/cancelar")
                                                .hasAnyRole("ADMIN", "CLIENTE")

                                                // Gestión de clases: solo administrador y entrenador
                                                .requestMatchers("/clases", "/clases/**")
                                                .hasAnyRole("ADMIN", "ENTRENADOR")

                                                // Reservaciones: cliente
                                                .requestMatchers(
                                                                "/reservaciones/nueva",
                                                                "/reservaciones/guardar",
                                                                "/reservaciones/mis-reservaciones")
                                                .hasRole("CLIENTE")

                                                // Gestión de usuarios: administrador
                                                .requestMatchers(
                                                                "/usuarios/nuevo",
                                                                "/usuarios/*/editar",
                                                                "/usuarios/*/actualizar",
                                                                "/usuarios/*/eliminar")
                                                .hasRole("ADMIN")

                                                // Perfil del cliente autenticado (CLIENTE y ADMIN)
                                                .requestMatchers("/perfil", "/usuarios/perfil",
                                                                "/perfil/actualizar", "/usuarios/perfil/actualizar")
                                                .hasAnyRole("CLIENTE", "ADMIN")

                                                // Consulta de usuarios
                                                .requestMatchers(
                                                                "/usuarios",
                                                                "/usuarios/*")
                                                .authenticated()

                                                // Cualquier otra ruta requiere autenticación
                                                .anyRequest().authenticated())

                                // Inicio de sesión
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/usuarios", true)
                                                .failureUrl("/login?error")
                                                .permitAll())

                                // Cierre de sesión
                                .logout(logout -> logout
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll())

                                // Protección CSRF
                                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))

                                .headers(h -> h.frameOptions(f -> f.sameOrigin()));

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

}
