package com.atoss.idea.management.system.user;

import com.atoss.idea.management.system.exception.UserNotFoundException;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.UserPageDTO;
import com.atoss.idea.management.system.repository.dto.UserResponseDTO;
import com.atoss.idea.management.system.repository.dto.UserUpdateDTO;
import com.atoss.idea.management.system.repository.entity.Avatar;
import com.atoss.idea.management.system.repository.entity.Role;
import com.atoss.idea.management.system.repository.entity.User;

import com.atoss.idea.management.system.service.implementation.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserServiceImpl userService;

    @Spy
    UserUpdateDTO userUpdateDTO;


    @Spy
    ModelMapper modelMapper;
    @Spy
    Pageable pageable;

    User user;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllUsersTest() {
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
        List<User> users = List.of(user);
        Page<User> usersPageTest = new PageImpl<>(users);
        Mockito.when(userRepository.findAll(pageable)).thenReturn(usersPageTest);
        Page<UserResponseDTO> userResponseDTOPage = userService.getAllUsers(pageable);
        assertEquals(1, userResponseDTOPage.getTotalElements());
    }

    @Test
    public void getAllUsersByUsernameTest() {
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
        Mockito.when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));
        UserResponseDTO userResponseDTOPage = userService.getUserByUsername("testName");
        assertEquals("testName", userResponseDTOPage.getUsername());
    }

    @Test
    public void getAllUsersByUsernameWhenUserNotFound() {
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
        Mockito.when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername("testName"));
    }

//    @Test
//    public void updateUserByUsernameTest() {
//        User user = new User();
//        user.setUsername("testName");
//        user.setFullName("test");
//        user.setEmail("test@yahoo.mail");
//        user.setIsActive(true);
//        user.setPassword("test");
//        user.setAvatar(new Avatar());
//        user.setId(1l);
//        user.setRole(Role.STANDARD);
//        user.setIsFirstLogin(false);
//        user.setHasPassword(true);
//
//        Mockito.when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));
//        Mockito.when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));
//        Mockito.when(userRepository.save(user)).thenReturn(user);
//
//        UserResponseDTO userResponseDTO = userService.updateUserByUsername("test", userUpdateDTO);
//        assertEquals("testName", userResponseDTO.getUsername());
    //}

    @Test
    public void getAllUsersForAdminTest() {
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
        List<User> users = List.of(user);
        Page<User> usersPageTest = new PageImpl<>(users);
        Mockito.when(userRepository.findAll(pageable)).thenReturn(usersPageTest);
        UserPageDTO userPageDTO = userService.getAllUsersForAdmin(pageable);
        assertEquals(1, userPageDTO.getPagedUsers().getContent().size());
    }

    @Test
    public void getAllUsersByUsernamePageableTest() {
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
        List<User> users = List.of(user);
        pageable = PageRequest.of(0, 5,Sort.by(Sort.DEFAULT_DIRECTION, "username"));
        Page<User> usersPageTest = new PageImpl<>(users);
        Mockito.when(userRepository.findByUsernameStartsWithOrderByIsActiveAscIdAsc(any(String.class), any(Pageable.class))).thenReturn(usersPageTest);
        UserPageDTO userPageDTO = userService.getAllUsersByUsernamePageable(pageable, "testName");
        assertEquals(5, userPageDTO.getPagedUsers().getSize());
        assertEquals(1, userPageDTO.getTotal());

    }

    @Test
    public void getAllPendingUsersTest(){
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
        List<User> users = List.of(user);
        Page<User> usersPageTest = new PageImpl<>(users);
        Mockito.when(userRepository.findAll(any(Pageable.class))).thenReturn(usersPageTest);
        Page<UserResponseDTO> userResponseDTOPage = userService.getAllPendingUsers(true,pageable);
        assertEquals(1,  userResponseDTOPage.getTotalElements());
    }

}