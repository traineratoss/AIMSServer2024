package com.atoss.idea.management.system.test;

import com.atoss.idea.management.system.controller.UserController;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.UserResponseDTO;
import com.atoss.idea.management.system.repository.entity.Avatar;
import com.atoss.idea.management.system.repository.entity.Role;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.SendEmailService;
import com.atoss.idea.management.system.service.implementation.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;

public class ChangeRoleTest {
    @Mock
     UserRepository userRepository;

    @Spy
     ModelMapper modelMapper;

    @InjectMocks
     UserServiceImpl userService;

    @Mock
    SendEmailService sendEmailServicetest;

    UserController userController;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        userController = spy(new UserController(userService , sendEmailServicetest) );
    }

    @Test
    public void testChangeRoletoAdmin(){

        User user = new User();
        user.setUsername("testName");
        user.setFullName("test");
        user.setEmail("test@yahoo.mail");
        user.setIsActive(true);
        user.setPassword("test");
        user.setAvatar(new Avatar());
        user.setId(1l);
        user.setRole(Role.STANDARD);
        user.setIsFirstLogin(false);
        user.setHasPassword(true);
        Optional<User> userOptional = Optional.of(user);
        Mockito.when(userRepository.findByUsername("testRole")).thenReturn(userOptional);
        UserResponseDTO userResponseDTOTest = userController.updateUserRole("testRole");
        assertEquals(Role.ADMIN, userResponseDTOTest.getRole());
    }

    @Test
    public void testChangeRoletoStandard() {

        User user = new User();
        user.setUsername("testName");
        user.setFullName("test");
        user.setEmail("test@yahoo.mail");
        user.setIsActive(true);
        user.setPassword("test");
        user.setAvatar(new Avatar());
        user.setId(1l);
        user.setRole(Role.ADMIN);
        user.setIsFirstLogin(false);
        user.setHasPassword(true);
        Optional<User> userOptional = Optional.of(user);
        Mockito.when(userRepository.findByUsername("testRole")).thenReturn(userOptional);
        UserResponseDTO userResponseDTOTest = userController.updateUserRole("testRole");
        assertEquals(Role.STANDARD, userResponseDTOTest.getRole());
    }

}
