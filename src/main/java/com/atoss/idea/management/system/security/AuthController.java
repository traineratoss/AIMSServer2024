package com.atoss.idea.management.system.security;

import com.atoss.idea.management.system.exception.EmailAlreadyExistException;
import com.atoss.idea.management.system.exception.UsernameAlreadyExistException;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.entity.Role;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.security.request.LoginRequest;
import com.atoss.idea.management.system.security.request.SignupRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @Transactional
    @PostMapping("/login")
    public String authenticateUser(@RequestBody LoginRequest authRequest, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            String email = authentication.getAuthorities().iterator().next().getAuthority();
            String fullName = authentication.getAuthorities().iterator().next().getAuthority();
            String avatarId = authentication.getAuthorities().iterator().next().getAuthority();
            Cookie userCookie = new Cookie("user_info", username + ":" + role + ":" + email + ":" + fullName + ":" + avatarId);
            userCookie.setMaxAge(3600);
            response.addCookie(userCookie);
        }
        return "Authentication successful!";
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