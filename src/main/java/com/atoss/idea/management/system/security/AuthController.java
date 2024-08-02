package com.atoss.idea.management.system.security;

import com.atoss.idea.management.system.exception.EmailAlreadyExistException;
import com.atoss.idea.management.system.exception.UsernameAlreadyExistException;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.UserSecurityDTO;
import com.atoss.idea.management.system.repository.entity.RefreshToken;
import com.atoss.idea.management.system.repository.entity.Role;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.security.request.LoginRequest;
import com.atoss.idea.management.system.security.request.RegisterRequest;
import com.atoss.idea.management.system.security.response.AuthResponse;
import com.atoss.idea.management.system.security.token.JwtService;
import com.atoss.idea.management.system.security.token.RefreshTokenService;
import com.atoss.idea.management.system.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final UserService userService;
    private final CookieService cookieService;

    /**
     * Constructor for the AuthController class.
     *
     * @param authenticationManager The AuthenticationManager used for handling authentication requests.
     * @param userService           The UserRepository used for accessing user-related data and operations.
     * @param refreshTokenService   The RefreshTokenService used for handling refresh tokens.
     * @param jwtService            The JwtService used for generating and verifying tokens.
     * @param cookieService         The CookieService used for creating access and refresh token
     * @see AuthenticationManager
     * @see UserRepository
     * @see AuthController
     * @see CookieService
     */
    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService, RefreshTokenService refreshTokenService, JwtService jwtService, CookieService cookieService) {
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.userService = userService;
        this.cookieService = cookieService;
    }

    /**
     * Authenticates a user based on the provided login request and sets user information as a cookie in the response.
     * This method is used to authenticate a user based on the provided LoginRequest object.
     * The authentication process may involve verifying the user's credentials, such as username and password,
     * using the AuthenticationManager provided in the constructor or by Spring Security configuration.
     * If the user's authentication is successful, the method sets user information as a cookie in the response.
     * The user information stored in the cookie may include the username, user role, email, full name, and avatar ID,
     * which can be used to identify and personalize user interactions within the application.
     * The method may return a String indicating the result of the authentication process, such as "Authentication successful!",
     * or return an error message or throw an exception in case of authentication failure.
     *
     * @param loginRequest The LoginRequest object containing the user's authentication credentials.
     * @param response    The HttpServletResponse object to set the user information as a cookie.
     *
     * @return A String indicating the result of the authentication process.
     *         If the authentication is successful, the method returns "Authentication successful!".
     *         If the authentication fails, the method throw AuthenticationException,
     *
     * @throws AuthenticationException
     *
     * @see LoginRequest
     * @see AuthenticationManager
     * @see SecurityContextHolder
     * @see Authentication
     * @see HttpServletResponse
     */
    @Transactional
    @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(),
                       loginRequest.getPassword()));

        if (authentication.isAuthenticated()) {

            String accessToken = jwtService.generateToken(authentication.getName());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authentication.getName());

            ResponseCookie cookie = cookieService.createTokenCookie(
                    cookieService.getIdentifier(jwtService.getTokenConfig().getType(), null),
                    accessToken,
                    jwtService.getTokenConfig());
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            ResponseCookie cookieRefresh = cookieService.createTokenCookie(
                    cookieService.getIdentifier(refreshTokenService.getTokenConfig().getType(), null),
                    refreshToken.getToken(),
                    refreshTokenService.getTokenConfig());
            response.addHeader(HttpHeaders.SET_COOKIE, cookieRefresh.toString());

            UserSecurityDTO userData = userService.getUserByUsername(authentication.getName(), UserSecurityDTO.class);

            return new ResponseEntity<>(
                    new AuthResponse(
                      jwtService.extractExpiration(accessToken),
                      Date.from(refreshToken.getExpiryDate()),
                      userData
                    ),
                    HttpStatus.OK
            );
        } else {
            throw new UsernameNotFoundException("Bad request");
        }
    }

    /**
     ** Registers a new user in the system based on the provided signup request.
     *
     * @param signUpRequest The SignupRequest object containing the user's registration information.
     *
     * @return A ResponseEntity representing the result of the user registration.
     *
     * @throws UsernameAlreadyExistException If the username provided in the signup request already exists in the system.
     * @throws EmailAlreadyExistException    If the email provided in the signup request already exists in the system.
     *
     * @see ResponseEntity
     * @see RegisterRequest
     * @see User
     * @see Role
     * @see UsernameAlreadyExistException
     * @see EmailAlreadyExistException
     */
    @Transactional
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        return new ResponseEntity<>(
                userService.addUser(signUpRequest.getUsername(), signUpRequest.getEmail()),
                HttpStatus.CREATED);
    }

    /**
     * Generates a new refresh token in the system
     * @param request The HttpServletRequest contains the request details
     * @param response The HttpServletResponse contains the response details
     * @return ResponseEntity contains the authResponse and an HTTP ok status
     */
    @Transactional
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {

        AuthResponse authResponse = refreshTokenService.refreshAuthToken(request, response);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);

    }


    /**
     * Logs out a user by invalidating the session. This encompasses invalidating the refresh token
     * and blacklisting the access token, preventing any further usage thereof for authentication purposes.
     *
     * @param request The HttpServletRequest containing the request details
     * @return ResponseEntity containing a string confirming a successful logout.
     */
    @Transactional
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletRequest request) {

        cookieService.getTokenFromCookies(request.getCookies(), "refreshToken")
                .ifPresent(refreshTokenService::invalidateToken);

        cookieService.getTokenFromCookies(request.getCookies(), "accessToken")
                .ifPresent(jwtService::invalidateToken);

        return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
    }

}