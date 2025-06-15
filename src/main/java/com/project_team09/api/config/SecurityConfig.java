package com.project_team09.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public UserDetailsService userDetailsService() {
                UserDetails admin = User.builder()
                                .username("admin")
                                .password(passwordEncoder().encode("admin"))
                                .roles("ADMIN")
                                .build();

                UserDetails tester = User.builder()
                                .username("tester")
                                .password(passwordEncoder().encode("tester"))
                                .roles("TESTER")
                                .build();

                UserDetails developer = User.builder()
                                .username("developer")
                                .password(passwordEncoder().encode("developer"))
                                .roles("DEVELOPER")
                                .build();

                // Adding previous Keycloak user
                UserDetails emailUser = User.builder()
                                .username("hanbugra@yahoo.com")
                                .password(passwordEncoder().encode("12345678"))
                                .roles("ADMIN")
                                .build();
                return new InMemoryUserDetailsManager(admin, tester, developer, emailUser);
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.addAllowedOrigin("http://localhost:3000");
                configuration.addAllowedMethod("*");
                configuration.addAllowedHeader("*");
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.cors().configurationSource(corsConfigurationSource())
                                .and()
                                .csrf().disable().authorizeRequests()
                                .antMatchers("/auth/**").permitAll() // Updated path without /api prefix
                                .antMatchers("/api/auth/**").permitAll() // Keep old path for compatibility
                                .antMatchers("/api/file-tracking/**").permitAll() // Allow file tracking endpoints
                                .antMatchers("/api/templates/**").permitAll() // Allow access to template downloads
                                .antMatchers("/api/product-backlog/**").permitAll() // Allow access to product backlog
                                                                                    // endpoints
                                .antMatchers("/api/admin/**").hasRole("ADMIN")
                                .antMatchers("/api/test-runs/**").hasAnyRole("ADMIN", "TESTER")
                                .antMatchers("/api/test-results/**").hasAnyRole("ADMIN", "TESTER", "DEVELOPER")
                                .antMatchers("/api/**").authenticated()
                                .anyRequest().permitAll()
                                .and()
                                .httpBasic();

                return http.build();
        }
}
