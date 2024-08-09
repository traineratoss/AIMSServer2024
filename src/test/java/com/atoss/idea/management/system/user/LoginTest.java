package com.atoss.idea.management.system.user;

import com.atoss.idea.management.system.repository.AvatarRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.security.AuthController;
import com.atoss.idea.management.system.security.SessionService;
import com.atoss.idea.management.system.security.UserDetailsServiceImpl;
import com.atoss.idea.management.system.security.WebSecurityConfig;
import com.atoss.idea.management.system.security.request.LoginRequest;
import com.atoss.idea.management.system.security.token.JwtService;
import com.atoss.idea.management.system.security.token.RefreshTokenService;
import com.atoss.idea.management.system.service.SendEmailService;
import com.atoss.idea.management.system.service.UserService;
import com.atoss.idea.management.system.service.implementation.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;

public class LoginTest {

    UserService spyUserService;

    AuthController spyAuthController;

    @Mock
    SendEmailService mockSendEmailService;

    @Mock
    UserRepository mockUserRepository;

    @Spy
    ModelMapper spyModelMapper;

    @Mock
    AvatarRepository mockAvatarRepository;

    private String userNameOrEmail;
    String password = "$2a$10$QkRidA35ea0Fzm/ObrOEgOPc.iR5bVFykS69zBoIFU/2DMhsBVf2O";

    User user;

    WebSecurityConfig spyWebSecurityConfig;

    @Mock
    RefreshTokenService mockRefreshTokenService;

    @Mock
    JwtService mockJwtService;

    @Mock
    SessionService mockSessionService;

    @Mock
    HttpServletRequest httpServletRequest;

    @Mock
    HttpServletResponse httpServletResponse;

    UserDetailsServiceImpl spyUserDetailsService;

    LoginRequest loginRequest;

    ProviderManager spyProviderManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        spyUserDetailsService = spy(
                new UserDetailsServiceImpl(
                        mockUserRepository
                )
        );

        spyUserService = spy(
                new UserServiceImpl(
                        mockUserRepository,
                        spyModelMapper,
                        mockSendEmailService,
                        mockAvatarRepository
                )
        );

        spyWebSecurityConfig = spy(
                new WebSecurityConfig(
                        spyUserDetailsService,
                        null,
                        mockJwtService,
                        mockSessionService,
                        new String[]{},
                        new String[]{}
                )
        );

        AuthenticationProvider authenticationProvider = spyWebSecurityConfig.authenticationProvider();
        spyProviderManager = spy(
            new ProviderManager(authenticationProvider)
        );

        spyAuthController = spy(
                new AuthController(
                        spyProviderManager,
                        spyUserService,
                        mockRefreshTokenService,
                        mockJwtService,
                        mockSessionService
                )
        );

        user = new User();

        loginRequest = new LoginRequest();
        loginRequest.setPassword("1Adminuser@");
        loginRequest.setUsernameOrEmail(userNameOrEmail);

    }

    @Test
    void testLoginWithDeactivatedUser() {
        user.setIsActive(false);
        user.setPassword(password);

        Mockito.when(mockUserRepository.findByUsernameOrEmail(userNameOrEmail, userNameOrEmail))
                .thenReturn(Optional.ofNullable(user));

        assertThrows(BadCredentialsException.class, () -> spyAuthController.login(
                loginRequest,
                httpServletRequest,
                httpServletResponse
        ));
    }

    @Test
    void testLoginWithBadCredentials() {
        user.setPassword("$2a$10$QkRidA35ea0Fzm/ObrOEgOzj/N.ZTS3.GHk/6vqddMSthHEaDni71");

        assertThrows(BadCredentialsException.class, () -> spyAuthController.login(
                loginRequest,
                httpServletRequest,
                httpServletResponse
        ));
    }
}
