package com.organization.org_voting_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // REQUIRED for @PreAuthorize
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationSuccessHandler successHandler;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UserDetailsService userDetailsService,
                          CustomAuthenticationSuccessHandler successHandler,
                          PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.successHandler = successHandler;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/login", "/register", "/forgot-password",
                    "/css/**", "/js/**", "/images/**"
                ).permitAll()
                .requestMatchers("/voter/**").hasRole("VOTER")
                .requestMatchers("/election-officer/**").hasRole("ELECTION_OFFICER")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler)   // ✅ role-based redirect
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/login?error=access_denied")
            )
            .csrf(csrf -> csrf.disable())
            .authenticationProvider(authenticationProvider());  // Use the DaoAuthenticationProvider

        return http.build();
    }
}
