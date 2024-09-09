package org.example.abb_interview_task.security;

import lombok.RequiredArgsConstructor;
import org.example.abb_interview_task.filter.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] AUTH_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
    };
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthFilter jwtAuthFiler;

    @Bean
    @SuppressWarnings("all")
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(
                        //AUTH
                        request -> request
                                .requestMatchers(AUTH_WHITELIST)
                                .permitAll()
                                .requestMatchers("/api/auth/login", "/api/auth/register")
                                .permitAll()
                                .requestMatchers("/api/auth/{userName}")
                                .authenticated()
                )
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers("/api/cards/use/**")
                                .permitAll()
                )
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers("/api/**")
                                .authenticated()
                )
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers("/files/**")
                                .authenticated()
                )

                .logout(
                        request -> request.
                                logoutUrl("/api/auth/logout")
                                .logoutSuccessHandler(
                                        new HttpStatusReturningLogoutSuccessHandler()
                                ).logoutSuccessUrl("/")
                )
                .sessionManagement()
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFiler, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
