package com.atoss.idea.management.system.security;

import com.atoss.idea.management.system.repository.entity.Role;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig implements WebMvcConfigurer {
    private final UserDetailsService userDetailsService;

    private final AuthEntryPoint unauthorizedHandler;

    private final JwtService jwtService;

    private CookieService cookieService;




    /**
     * Creates a BCryptPasswordEncoder bean for encoding passwords.
     *
     * @return A BCryptPasswordEncoder object for password encoding.
     * @see PasswordEncoder
     * @see BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates an AuthTokenFilter bean for filtering and validating JWT authentication tokens.
     *
     * @return An AuthTokenFilter object for filtering and validating JWT authentication tokens.
     * @see AuthFilter
     */
    @Bean
    public AuthFilter authenticationJwtTokenFilter() {
        return new AuthFilter(jwtService, userDetailsService, cookieService);
    }

    /**
     * Creates a DaoAuthenticationProvider bean for custom authentication using UserDetailsServiceImpl.
     *
     * @return A DaoAuthenticationProvider object for custom authentication using UserDetailsServiceImpl.
     * @see DaoAuthenticationProvider
     * @see UserDetailsServiceImpl
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Creates a custom AuthenticationManager bean to manage user authentication.
     *
     * @param authConfig The AuthenticationConfiguration to configure the authentication manager.
     * @return An AuthenticationManager object to manage user authentication.
     * @throws Exception If an exception occurs while configuring the authentication manager.
     * @see AuthenticationManager
     * @see AuthenticationConfiguration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configures the security filter chain for HTTP requests.
     *
     * @param http The HttpSecurity object used for configuring the security filter chain.
     * @return A SecurityFilterChain object representing the configured security filter chain.
     * @throws Exception If an exception occurs during the configuration of the security filter chain.
     * @see HttpSecurity
     * @see HttpMethod
     * @see UsernamePasswordAuthenticationFilter
     * @see AuthFilter
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers("/api/v1/auth/**")
                                .permitAll()
                                .requestMatchers(HttpMethod.OPTIONS, "/**")
                                .permitAll()
                                .requestMatchers(
                                        "/users/**",
                                        "/aims/api/v1/ideas/**",
                                        "/aims/api/v1/avatars/**",
                                        "/aims/api/v1/statistics/**",
                                        "/aims/api/v1/images/**")
                                .hasAnyRole(Role.STANDARD.name(), Role.ADMIN.name())
                                .requestMatchers("/**")
                                .hasRole(Role.ADMIN.name())
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}