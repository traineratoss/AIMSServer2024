package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.*;
import com.atoss.idea.management.system.repository.AvatarRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.*;
import com.atoss.idea.management.system.repository.entity.Avatar;
import com.atoss.idea.management.system.repository.entity.OTP;
import com.atoss.idea.management.system.repository.entity.Role;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.SendEmailService;
import com.atoss.idea.management.system.service.UserService;
import com.atoss.idea.management.system.utils.PasswordGenerator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final SendEmailService sendEmailService;

    private final AvatarRepository avatarRepository;

    @Value("${aims.app.bcrypt.salt}")
    private String bcryptSalt;

    /**
     * CONSTRUCTOR
     * @param userRepository for accessing CRUD repository methods for User Entity
     * @param modelMapper for mapping entity-dto relationships
     * @param sendEmailService service used for sending emails
     * @param avatarRepository for accessing CRUD repository methods for Avatar Entity
     */
    public UserServiceImpl(UserRepository userRepository,
                           ModelMapper modelMapper,
                           SendEmailService sendEmailService,
                           AvatarRepository avatarRepository) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.sendEmailService = sendEmailService;
        this.avatarRepository = avatarRepository;
    }

    @Override
    public UserResponseDTO addUser(String username, String email) {
        if (userRepository.findByUsernameOrEmail(username, email).isPresent()) {
            throw new UserAlreadyExistException("User already exists!");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistException("Email already exist!");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setIsActive(false);
        user.setHasPassword(false);
        user.setIsFirstLogin(true);
        userRepository.save(user);
        sendEmailService.sendEmailToUser(username);
        sendEmailService.sendEmailToAdmins(username);

        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO updateUserByUsername(String username, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (userUpdateDTO.getUsername() != null) {
            if (userRepository.findByUsername(userUpdateDTO.getUsername()).isEmpty()) {
                user.setUsername(userUpdateDTO.getUsername());
            } else {
                throw new UsernameAlreadyExistException("Username already exists!");
            }
        }
        if (userUpdateDTO.getEmail() != null) {
            if (userRepository.findByEmail(userUpdateDTO.getEmail()).isEmpty()) {
                user.setEmail(userUpdateDTO.getEmail());
            } else {
                throw new EmailAlreadyExistException("Email already exists!");
            }
        }
        if (userUpdateDTO.getAvatarId() != null) {
            Avatar avatar = avatarRepository
                    .findById(
                        userUpdateDTO.getAvatarId()
                    )
                    .orElseThrow(
                            () -> new AvatarNotFoundException("Avatar not found!")
                    );
            user.setAvatar(modelMapper.map(avatar, Avatar.class));
        }
        if (userUpdateDTO.getUpdatedImage()) {
            user.setImage(userUpdateDTO.getImage());
        }
        if (userUpdateDTO.getFullName() != null) {
            user.setFullName(userUpdateDTO.getFullName());
        }
        userRepository.save(user);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO updateUserRole(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found "));

        if (user.getRole() == Role.ADMIN) {
            user.setRole(Role.STANDARD);
        } else {
            user.setRole(Role.ADMIN);
        }
        userRepository.save(user);

        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public <T> T getUserByUsername(String username, Class<T> type) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        return modelMapper.map(user, type);
    }

    @Override
    public UserPageDTO getAllUsersForAdmin(Pageable pageable) {
        UserPageDTO userPageDTO = new UserPageDTO();
        userPageDTO.setTotal(userRepository.findAll().size());
        List<UserAdminDashboardResponseDTO> result = userRepository.findAll(pageable)
                                    .stream()
                                    .map(user -> modelMapper.map(user, UserAdminDashboardResponseDTO.class))
                                    .toList();
        return new UserPageDTO(userPageDTO.getTotal(), new PageImpl(result, pageable, result.size()));
    }

    @Override
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return new PageImpl<>(
                userRepository.findAll(pageable)
                        .stream()
                        .map(user -> modelMapper.map(user, UserResponseDTO.class))
                        .toList()
        );
    }

    @Override
    public UserPageDTO getAllUsersByUsernamePageable(
            Pageable pageable,
            String username
    ) {
        UserPageDTO userPageDTO = new UserPageDTO();
        userPageDTO.setTotal(userRepository.findByUsernameStartsWithOrderByIsActiveAscIdAsc(username, Pageable.unpaged()).getContent().size());
        List<UserAdminDashboardResponseDTO> result = userRepository
                .findByUsernameStartsWithOrderByIsActiveAscIdAsc(username, pageable)
                .stream()
                .map(user -> modelMapper.map(user, UserAdminDashboardResponseDTO.class))
                .toList();
        return new UserPageDTO(userPageDTO.getTotal(), new PageImpl(result, pageable, result.size()));
    }

    //    @Override
    //    public Page<UserAdminDashboardResponseDTO> getAllUsersByUsername(String username) {
    //        List<UserAdminDashboardResponseDTO> list = userRepository
    //                .findByUsernameStartsWith(username)
    //                .stream()
    //                .map(user -> modelMapper.map(user, UserAdminDashboardResponseDTO.class))
    //                .toList();
    //        System.out.println(list);
    //        return new PageImpl<>(
    //                userRepository.findByUsernameStartsWith(username)
    //                        .stream()
    //                        .map(user -> modelMapper.map(user, UserAdminDashboardResponseDTO.class))
    //                        .toList()
    //        );
    //    }

    @Override
    public Page<UserResponseDTO> getAllPendingUsers(boolean isActive, Pageable pageable) {
        return new PageImpl<>(
          userRepository.findAll(pageable)
                  .stream()
                  .filter(user -> user.getIsActive() != null && user.getIsActive().equals(isActive))
                  .map(user -> modelMapper.map(user, UserResponseDTO.class))
                  .toList()
        );
    }

    @Override
    public boolean changePassword(ChangePasswordDTO changePasswordDTO) {
        String username = changePasswordDTO.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (!user.getIsFirstLogin() && !BCrypt.checkpw(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new IncorrectPasswordException("The old password is incorrect!");
        }
        if (user.getIsFirstLogin() && BCrypt.checkpw(changePasswordDTO.getNewPassword(), user.getPassword())) {
            throw new IncorrectPasswordException("The new password cannot be the same as the old password!");
        }
        String hashFrontendNewPassword = BCrypt.hashpw(changePasswordDTO.getNewPassword(), bcryptSalt);
        user.setPassword(hashFrontendNewPassword);
        user.setIsFirstLogin(false);
        userRepository.save(user);
        return true;
    }

    @Override
    public UserSecurityDTO verifyOTP(VerifyOtpDTO verifyOtpDTO) {
        String usernameOrEmail = verifyOtpDTO.getUsernameOrEmail();
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        OTP otp = user.getOtp();
        if (otp == null || !otp.getCode().equals(verifyOtpDTO.getOtpCode())) {
            throw new BadCredentialsException("Bad credentials");
        }

        if (System.currentTimeMillis() - otp.getCreationDate() >= TimeUnit.MINUTES.toMillis(3)) {
            throw new BadCredentialsException("Expired OTP");
        }

        user.setIsFirstLogin(true);
        user.setOtp(null);
        user.setIsFirstLogin(true);

        userRepository.save(user);

        userRepository.save(user);

        return modelMapper.map(user, UserSecurityDTO.class);
    }

    @Override
    public ResponseEntity<Object> sendApproveEmail(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found exception"));
        if (!user.getHasPassword()) {
            sendEmailService.sendApproveEmailToUser(username);
            return new ResponseEntity<>("User approve successfully", HttpStatus.OK);
        }
        throw new ApproveAlreadyGrantedException("Approve already granted exception");
    }

    @Override
    public ResponseEntity<Object> sendDeclineEmail(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found exception"));
        if (!user.getHasPassword()) {
            sendEmailService.sendDeclineEmailToUser(username);
            if (deleteUser(username)) {
                return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
            }
            throw new UserAlreadyActivatedException("User status is active");
        }
        throw new ApproveAlreadyGrantedException("Approve already granted exception");
    }

    @Override
    public UserSecurityDTO login(String usernameOrEmail, String password) {
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getIsActive() && password.equals(user.getPassword())) {
                return modelMapper.map(
                        user,
                        UserSecurityDTO.class
                );
            }
            if (!user.getIsActive() && password.equals(user.getPassword())) {
                throw new UserAlreadyDeactivatedException("User was deactivated");
            }
        }
        throw new BadCredentialsException("Bad credentials");
    }

    @Override
    public ResponseEntity<Object> sendForgotPassword(String usernameOrEmail) {
        User user = userRepository
                .findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UserNotFoundException("User or email not found"));
        if (user.getIsActive()) {
            OTP otp = new OTP();
            otp.setCode(PasswordGenerator.generateOTP(6));
            otp.setCreationDate(System.currentTimeMillis());

            sendEmailService.sendEmailForgotPassword(user.getUsername(), otp.getCode());

            user.setOtp(otp);
            userRepository.save(user);
            return new ResponseEntity<>("Email sent", HttpStatus.OK);
        } else {
            if (user.getHasPassword()) {
                throw new UserAlreadyDeactivatedException("User was deactivated");
            }
            throw new BadCredentialsException("User not activated");
        }
    }

    @Override
    public Boolean deleteUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (user.getIsActive()) {
            return false;
        }
        userRepository.delete(user);
        return true;
    }

    @Override
    public ResponseEntity<Object> sendDeactivateEmail(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (user.getIsActive()) {
            if (sendEmailService.sendDeactivateEmailToUser(username)) {
                return new ResponseEntity<>("Email send", HttpStatus.OK);
            }
            throw new EmailFailedException("Sending email failed");
        }
        throw new UserAlreadyDeactivatedException("User already deactivate exception");
    }

    @Override
    public ResponseEntity<Object> sendActivateEmail(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (!user.getIsActive()) {
            if (sendEmailService.sendActivateEmailToUser(username)) {
                return new ResponseEntity<>("Email send", HttpStatus.OK);
            }
            throw new EmailFailedException("Sending email failed");
        }
        throw new UserAlreadyActivatedException("User already activate exception");
    }

    @Override
    public Boolean isFirstLogin(String usernameOrEmail) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
        return user.getIsFirstLogin();
    }


    @Override
    public Long getIdByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
        return user.getId();
    }

    @Override
    public void abortChangePassword(ChangePasswordDTO changePasswordDTO) {
        User user = userRepository.findByUsername(changePasswordDTO.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        user.setIsFirstLogin(false);
        userRepository.save(user);
    }

    @Override
    public ImageDTO getAvatarByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
        return user.getImage();
    }
}
