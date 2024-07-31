package com.atoss.idea.management.system.repository.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;

    private final AuthEntryPoint unauthorizedHandler;

    /**
     * Constructor for the WebSecurityConfig class.
     *
     * @param userDetailsService  The UserDetailsServiceImpl used for loading user-specific data during authentication.
     * @param unauthorizedHandler The AuthEntryPointJwt used for handling unauthorized access and authentication errors.
     * @see UserDetailsServiceImpl
     * @see AuthEntryPoint
     */
    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, AuthEntryPoint unauthorizedHandler) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
    }

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
        return new AuthFilter();
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
     * Creates a custom AuthenticationSuccessHandler bean for handling successful authentication events.
     *
     * @return An AuthenticationSuccessHandler object for handling successful authentication events.
     * @see AuthenticationSuccessHandler
     * @see CustomAuthenticationSuccessHandler
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
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
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/**").permitAll());
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}