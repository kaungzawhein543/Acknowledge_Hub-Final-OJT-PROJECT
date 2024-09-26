package com.ace.configuration;

import com.ace.security.JwtAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.function.Supplier;
import java.util.logging.Logger;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger LOGGER = Logger.getLogger(SecurityConfig.class.getName());

    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    public SecurityConfig(@Lazy JwtAuthorizationFilter jwtAuthorizationFilter) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Publicly accessible endpoints
                        .requestMatchers("/auth/**", "/api/v1/announcement/all/**", "/api/v1/category/all/**",
                                "/api/v1/feedback/all/**", "/api/v1/feedback-reply/all/**", "/api/v1/email/**").permitAll()

                        // Secured endpoints for admins and HR
                        .requestMatchers("/api/v1/announcement/sys/**", "/api/v1/category/sys/**",
                                "/api/v1/company/sys/**", "/api/v1/department/sys/**",
                                "/api/v1/excel/sys/**", "/api/v1/group/sys/**",
                                "/api/v1/position/sys/**", "/api/v1/staff/sys/**").access(adminOrHrAuthorizationManager())

                        // Specific HR role authority
                        .requestMatchers("/api/v1/announcement/HRM/**", "/api/v1/category/HRM/**",
                                "/api/v1/feedback/HRM/**", "/api/v1/staff/HRM/**","/api/v1/group/HRM/**").hasAuthority("Human Resource(Main)")

                        // for both Human Resource(Main) and HR
                        .requestMatchers("/api/v1/staff/allHR/**").access(hrMainAndHrAuthorizationManager())

                        // Non-admin, non-HR access Human Resource(Main)
                        .requestMatchers("/api/v1/announcement/STF/**", "/api/v1/category/STF/**",
                                "/api/v1/staff/STF/**").access(notAdminOrHrMainAuthorizationManager())

                        //Admin,HR_Main and Normal Hr
                        .requestMatchers("/api/v1/staff/allSys/**").access(adminOrHrMainOrNormalHrAuthorizationManager())

                        // Catch-all authenticated requests
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Add JWT Authorization Filter before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // method for ADMIN and Human Resource(Main)
    @Bean
    public AuthorizationManager<RequestAuthorizationContext> adminOrHrAuthorizationManager() {
        return (Supplier<Authentication> authentication, RequestAuthorizationContext context) -> {
            Authentication auth = authentication.get();
            if (auth != null && auth.isAuthenticated()) {
                // Log the authorities for debugging
                auth.getAuthorities().forEach(authority ->
                        LOGGER.info("Authority: " + authority.getAuthority())
                );

                boolean isAdmin = auth.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
                boolean isHrMain = auth.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("Human Resource(Main)"));

                LOGGER.info("isAdmin: " + isAdmin + ", isHrMain: " + isHrMain);

                return new AuthorizationDecision(isAdmin || isHrMain);
            }
            return new AuthorizationDecision(false);
        };
    }

    //Method for Human Resource(Main) and HR
    @Bean
    public AuthorizationManager<RequestAuthorizationContext> hrMainAndHrAuthorizationManager() {
        return (authenticationSupplier, context) -> {
            Authentication auth = authenticationSupplier.get();
            if (auth != null && auth.isAuthenticated()) {
                boolean isHr = auth.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("Human Resource"));
                boolean isHrMain = auth.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("Human Resource(Main)"));

                return new AuthorizationDecision(isHrMain || isHr);
            }
            return new AuthorizationDecision(false);
        };
    }

    //Method for only Staff
    @Bean
    public AuthorizationManager<RequestAuthorizationContext> notAdminOrHrMainAuthorizationManager() {
        return (authenticationSupplier, context) -> {
            Authentication auth = authenticationSupplier.get();
            if (auth != null && auth.isAuthenticated()) {
                boolean isAdmin = auth.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
                boolean isHrMain = auth.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("Human Resource(Main)"));

                LOGGER.info(", user is: " + isHrMain);

                return new AuthorizationDecision(!(isAdmin || isHrMain));
            }
            return new AuthorizationDecision(false);
        };
    }

    //Method for ADMIN,HR_MAIN and Normal HR
    @Bean
    public AuthorizationManager<RequestAuthorizationContext> adminOrHrMainOrNormalHrAuthorizationManager() {
        return (authenticationSupplier, context) -> {
            Authentication auth = authenticationSupplier.get();
            if (auth != null && auth.isAuthenticated()) {
                boolean isAdmin = auth.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
                boolean isHr = auth.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("Human Resource"));
                boolean isHrMain = auth.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("Human Resource(Main)"));

                return new AuthorizationDecision(isAdmin || isHrMain || isHr);
            }
            return new AuthorizationDecision(false);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
