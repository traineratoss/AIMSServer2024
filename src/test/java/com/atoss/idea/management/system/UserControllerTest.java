package com.atoss.idea.management.system;

import com.atoss.idea.management.system.controller.UserController;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.UserResponseDTO;
import com.atoss.idea.management.system.repository.entity.Role;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.implementation.SendEmailServiceImpl;
import com.atoss.idea.management.system.service.implementation.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @Mock
    public UserRepository userRepository;

    @InjectMocks
    public UserServiceImpl userServiceImpl;

    @Mock
    public SendEmailServiceImpl sendEmailServiceImpl;

    @Spy
    public ModelMapper modelMapper;
    public UserController userController;
    public User user;



    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userServiceImpl, sendEmailServiceImpl);
    }

    @Test
    public void createUser(){
        user = new User();
        user.setId(1242141L);
        user.setUsername("catallinu");
        user.setFullName("Catalin Lupascu");
        user.setEmail("clupascu003@gmail.com");
        user.setPassword("123123");
        user.setRole(Role.STANDARD);

        when(userRepository.save(any(User.class))).thenReturn(user);

        ResponseEntity<UserResponseDTO> user2 = userController.addUser( "catallinu", "afasf@gmail.com");
        assertEquals(user2.getBody().getUsername(), "catallinu");
    }

}
