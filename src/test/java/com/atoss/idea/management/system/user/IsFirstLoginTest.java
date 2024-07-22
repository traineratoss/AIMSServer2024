package com.atoss.idea.management.system.user;

import com.atoss.idea.management.system.exception.UserNotFoundException;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.entity.Avatar;
import com.atoss.idea.management.system.repository.entity.Role;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.implementation.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IsFirstLoginTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @BeforeEach
    public void setUp() {

        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void TestIsFirstLogin() {
        User user = new User();
        user.setUsername("testName");
        user.setFullName("test");
        user.setEmail("test@yahoo.mail");
        user.setIsActive(true);
        user.setPassword("test");
        user.setAvatar(new Avatar());
        user.setId(1l);
        user.setRole(Role.STANDARD);
        user.setIsFirstLogin(true);
        user.setHasPassword(true);

        List<User> users = List.of(user);
        Mockito.when(userRepository.findByUsernameOrEmail(any(String.class), any(String.class))).thenReturn(Optional.of(user));
        Boolean test = userService.isFirstLogin("testName");
        assertEquals(true, test);

    }

    @Test
    public void TestIsFirstLoginWhenUserNotFound() {
        User user = new User();
        user.setUsername("testName");
        user.setFullName("test");
        user.setEmail("test@yahoo.mail");
        user.setIsActive(true);
        user.setPassword("test");
        user.setAvatar(new Avatar());
        user.setId(1l);
        user.setRole(Role.STANDARD);
        user.setIsFirstLogin(true);
        user.setHasPassword(true);

        List<User> users = List.of(user);
        Mockito.when(userRepository.findByUsernameOrEmail(any(String.class), any(String.class))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.isFirstLogin("testName"));

    }


}