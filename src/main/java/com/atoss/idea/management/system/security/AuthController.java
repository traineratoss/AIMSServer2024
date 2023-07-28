package com.atoss.idea.management.system.security;

import com.atoss.idea.management.system.exception.EmailAlreadyExistException;
import com.atoss.idea.management.system.exception.UsernameAlreadyExistException;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.UserSecurityDTO;
import com.atoss.idea.management.system.repository.entity.Role;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.security.request.LoginRequest;
import com.atoss.idea.management.system.security.request.SignupRequest;
import com.atoss.idea.management.system.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils;

    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder, JwtUtils jwtUtils, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @Transactional
    @PostMapping("/login")
    public ResponseEntity<HttpServletResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        UserSecurityDTO userSecurityDTO = userService.login(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());
        Cookie cookie = new Cookie("username", userSecurityDTO.getUsername());
        cookie.setMaxAge(60 * 60 * 24);
        cookie.setPath("/");
        response.addCookie(cookie);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
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