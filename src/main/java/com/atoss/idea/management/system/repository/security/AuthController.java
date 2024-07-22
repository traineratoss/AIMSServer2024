package com.atoss.idea.management.system.security;

import com.atoss.idea.management.system.exception.EmailAlreadyExistException;
import com.atoss.idea.management.system.exception.UsernameAlreadyExistException;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.entity.Role;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.security.request.LoginRequest;
import com.atoss.idea.management.system.security.request.RegisterRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    /**
     * Constructor for the AuthController class.
     *
     * @param authenticationManager The AuthenticationManager used for handling authentication requests.
     * @param userRepository        The UserRepository used for accessing user-related data and operations.
     *
     * @see AuthenticationManager
     * @see UserRepository
     * @see AuthController
     */
    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
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
     * @param authRequest The LoginRequest object containing the user's authentication credentials.
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
    @PostMapping("/login")
    public String authenticateUser(@RequestBody LoginRequest authRequest, HttpServletResponse response) {
        // Get the current authentication from the security context
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        // Check if the authentication is not null and the user is authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            // Retrieve user information from the authentication object
            String username = authentication.getName();
            // Get the user's role (authority)
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            // Get the user's email (authority)
            String email = authentication.getAuthorities().iterator().next().getAuthority();
            // Get the user's full name (authority)
            String fullName = authentication.getAuthorities().iterator().next().getAuthority();
            // Get the user's avatar ID (authority)
            String avatarId = authentication.getAuthorities().iterator().next().getAuthority();
            //
            String isFirstLogin = authentication.getAuthorities().iterator().next().getAuthority();
            // Create a user_info cookie containing user information
            Cookie userCookie = new Cookie("user_info", username + ":" + role + ":" + email + ":" + fullName + ":" + avatarId + ":" + isFirstLogin);
            userCookie.setMaxAge(3600);         // Set the maximum age of the cookie to 1 hour (3600 seconds)
            response.addCookie(userCookie);     // Add the user_cookie to the response
        }
        return "Authentication successful!";
    }

    /**
     * Registers a new user in the system based on the provided signup request.
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
        if (userRepository.findByUsername(signUpRequest.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistException("Username already exist!");
        }

        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyExistException("Email already exist!");
        }
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail());
        user.setRole(Role.STANDARD);
        userRepository.save(user);

        return new ResponseEntity<>("User created!", HttpStatus.CREATED);
    }
}