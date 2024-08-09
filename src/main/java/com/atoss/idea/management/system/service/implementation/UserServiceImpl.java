package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.ApproveAlreadyGrantedException;
import com.atoss.idea.management.system.exception.AvatarNotFoundException;
import com.atoss.idea.management.system.exception.EmailAlreadyExistException;
import com.atoss.idea.management.system.exception.EmailFailedException;
import com.atoss.idea.management.system.exception.IncorrectPasswordException;
import com.atoss.idea.management.system.exception.UserAlreadyActivatedException;
import com.atoss.idea.management.system.exception.UserAlreadyDeactivatedException;
import com.atoss.idea.management.system.exception.UserAlreadyExistException;
import com.atoss.idea.management.system.exception.UserNotFoundException;
import com.atoss.idea.management.system.exception.UsernameAlreadyExistException;
import com.atoss.idea.management.system.repository.AvatarRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.ChangePasswordDTO;
import com.atoss.idea.management.system.repository.dto.ImageDTO;
import com.atoss.idea.management.system.repository.dto.UserAdminDashboardResponseDTO;
import com.atoss.idea.management.system.repository.dto.UserPageDTO;
import com.atoss.idea.management.system.repository.dto.UserResponseDTO;
import com.atoss.idea.management.system.repository.dto.UserSecurityDTO;
import com.atoss.idea.management.system.repository.dto.UserUpdateDTO;
import com.atoss.idea.management.system.repository.dto.VerifyOtpDTO;
import com.atoss.idea.management.system.repository.entity.Avatar;
import com.atoss.idea.management.system.repository.entity.OTP;
import com.atoss.idea.management.system.repository.entity.Role;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.SendEmailService;
import com.atoss.idea.management.system.service.UserService;
import com.atoss.idea.management.system.utils.PasswordGenerator;
import lombok.extern.log4j.Log4j2;
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
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final SendEmailService sendEmailService;

    private final AvatarRepository avatarRepository;

    @Value("${aims.app.bcrypt.salt}")
    private String bcryptSalt;

    @Value("${aims.app.otpExpiryMinutes}")
    private Long otpExpiryMinutes;

    /**
     * CONSTRUCTOR
     *
     * @param userRepository   for accessing CRUD repository methods for User Entity
     * @param modelMapper      for mapping entity-dto relationships
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
            if (log.isErrorEnabled()) {
                log.error("Attempt to add user failed: User '{}' already exists", username);
            }
            throw new UserAlreadyExistException("User already exists!");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            if (log.isErrorEnabled()) {
                log.error("Attempt to add user failed: Email '{}' already exists", email);
            }
            throw new EmailAlreadyExistException("Email already exist!");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setIsActive(false);
        user.setHasPassword(false);
        user.setIsFirstLogin(true);
        userRepository.save(user);

        if (log.isInfoEnabled()) {
            log.info("New user added successfully: Username '{}', Email '{}'", username, email);
        }

        sendEmailService.sendEmailToUser(username);
        sendEmailService.sendEmailToAdmins(username);

        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO updateUserByUsername(String username, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            if (log.isErrorEnabled()) {
                log.error("User not found with username: {}", username);
            }
            return new UserNotFoundException("User not found");
        });
        if (userUpdateDTO.getUsername() != null) {
            if (userRepository.findByUsername(userUpdateDTO.getUsername()).isEmpty()) {
                user.setUsername(userUpdateDTO.getUsername());
                if (log.isInfoEnabled()) {
                    log.info("Updated username to: {}", userUpdateDTO.getUsername());
                }
            } else {
                if (log.isErrorEnabled()) {
                    log.error("Username '{}' already exists", userUpdateDTO.getUsername());
                }
                throw new UsernameAlreadyExistException("Username already exists!");
            }
        }
        if (userUpdateDTO.getEmail() != null) {
            if (userRepository.findByEmail(userUpdateDTO.getEmail()).isEmpty()) {
                user.setEmail(userUpdateDTO.getEmail());
                if (log.isInfoEnabled()) {
                    log.info("Updated email to: {}", userUpdateDTO.getEmail());
                }
            } else {
                if (log.isErrorEnabled()) {
                    log.error("Email '{}' already exists", userUpdateDTO.getEmail());
                }
                throw new EmailAlreadyExistException("Email already exists!");
            }
        }
        if (userUpdateDTO.getAvatarId() != null) {
            Avatar avatar = avatarRepository
                    .findById(
                            userUpdateDTO.getAvatarId()
                    )
                    .orElseThrow(() -> {
                        if (log.isErrorEnabled()) {
                            log.error("Avatar not found with id: {}", userUpdateDTO.getAvatarId());
                        }
                        return new AvatarNotFoundException("Avatar not found!");
                    });
            user.setAvatar(modelMapper.map(avatar, Avatar.class));
            if (log.isInfoEnabled()) {
                log.info("Updated avatar with file name: {}", avatar.getFileName());
            }
        }
        if (userUpdateDTO.getUpdatedImage()) {
            user.setImage(userUpdateDTO.getImage());
            if (log.isInfoEnabled()) {
                log.info("Updated user image");
            }
        }
        if (userUpdateDTO.getFullName() != null) {
            user.setFullName(userUpdateDTO.getFullName());
            if (log.isInfoEnabled()) {
                log.info("Updated full name to: {}", userUpdateDTO.getFullName());
            }
        }
        userRepository.save(user);

        if (log.isInfoEnabled()) {
            log.info("User with username '{}' successfully updated", username);
        }

        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO updateUserRole(String username) {

        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            if (log.isErrorEnabled()) {
                log.error("User not found with username: {}", username);
            }
            return new UserNotFoundException("User not found");
        });

        if (user.getRole() == Role.ADMIN) {
            user.setRole(Role.STANDARD);
        } else {
            user.setRole(Role.ADMIN);
        }
        userRepository.save(user);

        if (log.isInfoEnabled()) {
            log.info("Updated role for user with username: {}", username);
        }

        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public <T> T getUserByUsername(String username, Class<T> type) {

        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            if (log.isErrorEnabled()) {
                log.error("User not found with username: {}", username);
            }
            return new UserNotFoundException("User not found");
        });

        if (log.isInfoEnabled()) {
            log.info("User with username: {} found", username);
        }

        return modelMapper.map(user, type);
    }

    @Override
    public UserPageDTO getAllUsersForAdmin(Pageable pageable) {
        UserPageDTO userPageDTO = new UserPageDTO();
        int totalUsers = userRepository.findAll().size();
        userPageDTO.setTotal(totalUsers);
        List<UserAdminDashboardResponseDTO> result = userRepository.findAll(pageable)
                .stream()
                .map(user -> modelMapper.map(user, UserAdminDashboardResponseDTO.class))
                .toList();

        if (log.isInfoEnabled()) {
            log.info("Retrieved {} users for admin: {}", result.size(), result);
        }

        return new UserPageDTO(userPageDTO.getTotal(), new PageImpl(result, pageable, result.size()));
    }

    @Override
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        if (log.isInfoEnabled()) {
            log.info("Retrieved all users");
        }
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

        if (log.isInfoEnabled()) {
            log.info("Retrieved {} users with username starting with: {}", result.size(), username);
        }

        return new UserPageDTO(userPageDTO.getTotal(), new PageImpl(result, pageable, result.size()));
    }

    @Override
    public Page<UserResponseDTO> getAllPendingUsers(boolean isActive, Pageable pageable) {
        if (log.isInfoEnabled()) {
            log.info("Retrieved all pending users with isActive: {}", isActive);
        }
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
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            if (log.isErrorEnabled()) {
                log.error("User not found with username: {}", username);
            }
            return new UserNotFoundException("User not found!");
        });
        if (!user.getIsFirstLogin() && !BCrypt.checkpw(changePasswordDTO.getOldPassword(), user.getPassword())) {
            if (log.isErrorEnabled()) {
                log.error("Incorrect old password for user with username: {}", username);
            }
            throw new IncorrectPasswordException("The old password is incorrect!");
        }
        if (user.getIsFirstLogin() && BCrypt.checkpw(changePasswordDTO.getNewPassword(), user.getPassword())) {
            if (log.isErrorEnabled()) {
                log.error("This password has already being used! {}", username);
            }
            throw new IncorrectPasswordException("This password has already being used!");
        }
        String salt = BCrypt.gensalt();
        String hashFrontendNewPassword = BCrypt.hashpw(changePasswordDTO.getNewPassword(), salt);
        user.setPassword(hashFrontendNewPassword);
        user.setIsFirstLogin(false);
        userRepository.save(user);

        if (log.isInfoEnabled()) {
            log.info("Password successfully changed for user with username: {}", username);
        }

        return true;
    }

    @Override
    public UserSecurityDTO verifyOTP(VerifyOtpDTO verifyOtpDTO) {
        String usernameOrEmail = verifyOtpDTO.getUsernameOrEmail();
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> {
                    if (log.isErrorEnabled()) {
                        log.error("User not found with username or email: {}", usernameOrEmail);
                    }
                    return new UserNotFoundException("User not found!");
                });

        OTP otp = user.getOtp();
        if (otp == null || !otp.getCode().equals(verifyOtpDTO.getOtpCode())) {
            if (log.isErrorEnabled()) {
                log.error("Bad credentials: one-time password is null or does not match for user: {}", usernameOrEmail);
            }
            throw new BadCredentialsException("Bad credentials");
        }

        if (System.currentTimeMillis() - otp.getCreationDate() >= TimeUnit.MINUTES.toMillis(otpExpiryMinutes)) {
            if (log.isErrorEnabled()) {
                log.error("Expired one-time password for user: {}", usernameOrEmail);
            }
            throw new BadCredentialsException("Expired OTP");
        }

        user.setIsFirstLogin(true);
        user.setOtp(null);

        userRepository.save(user);

        if (log.isInfoEnabled()) {
            log.info("One-time password verified successfully for user: {}", usernameOrEmail);
        }

        return modelMapper.map(user, UserSecurityDTO.class);
    }

    @Override
    public ResponseEntity<Object> sendApproveEmail(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            if (log.isErrorEnabled()) {
                log.error("User not found with username: {}", username);
            }
            return new UserNotFoundException("User not found exception");
        });
        if (!user.getHasPassword()) {
            sendEmailService.sendApproveEmailToUser(username);

            if (log.isInfoEnabled()) {
                log.info("Approve email sent successfully to user with username: {}", username);
            }

            return new ResponseEntity<>("User approve successfully", HttpStatus.OK);
        }
        if (log.isErrorEnabled()) {
            log.error("Approve already granted for user with username: {}", username);
        }
        throw new ApproveAlreadyGrantedException("Approve already granted exception");
    }

    @Override
    public ResponseEntity<Object> sendDeclineEmail(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            if (log.isErrorEnabled()) {
                log.error("User not found with username: {}", username);
            }
            return new UserNotFoundException("User not found exception");
        });
        if (!user.getHasPassword()) {
            sendEmailService.sendDeclineEmailToUser(username);
            if (log.isInfoEnabled()) {
                log.info("Decline email sent successfully to user with username: {}", username);
            }
            if (deleteUser(username)) {
                if (log.isInfoEnabled()) {
                    log.info("User with username: {} deleted successfully", username);
                }
                return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
            }
            if (log.isErrorEnabled()) {
                log.info("Failed to delete user with username: {}", username);
            }
            throw new UserAlreadyActivatedException("User status is active");
        }
        if (log.isErrorEnabled()) {
            log.error("Approve already granted for user with username: {}", username);
        }
        throw new ApproveAlreadyGrantedException("Approve already granted exception");
    }

    @Override
    public ResponseEntity<Object> sendForgotPassword(String usernameOrEmail) {
        User user = userRepository
                .findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> {
                    if (log.isErrorEnabled()) {
                        log.error("User or email not found: {}", usernameOrEmail);
                    }
                    return new UserNotFoundException("User or email not found");
                });
        if (user.getIsActive()) {
            OTP otp = new OTP();
            otp.setCode(PasswordGenerator.generateOTP(6));
            otp.setCreationDate(System.currentTimeMillis());

            sendEmailService.sendEmailForgotPassword(user.getUsername(), otp.getCode());

            if (log.isInfoEnabled()) {
                log.info("Forgot password email sent to user with username: {} one-time password code: {}", user.getUsername(), otp.getCode());
            }

            user.setOtp(otp);
            userRepository.save(user);
            return new ResponseEntity<>("Email sent", HttpStatus.OK);
        } else {
            if (user.getHasPassword()) {
                if (log.isErrorEnabled()) {
                    log.error("User with username or email: {} was deactivated", usernameOrEmail);
                }
                throw new UserAlreadyDeactivatedException("User was deactivated");
            }
            if (log.isErrorEnabled()) {
                log.error("User with username or email: {} not activated", usernameOrEmail);
            }
            throw new BadCredentialsException("User not activated");
        }
    }

    @Override
    public Boolean deleteUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            if (log.isErrorEnabled()) {
                log.error("User not found with username: {}", username);
            }
            return new UserNotFoundException("User not found!");
        });
        if (user.getIsActive()) {
            if (log.isInfoEnabled()) {
                log.info("User with username: {} is active and cannot be deleted", username);
            }
            return false;
        }
        userRepository.delete(user);

        if (log.isInfoEnabled()) {
            log.info("User with username: {} has been deleted successfully", username);
        }

        return true;
    }

    @Override
    public ResponseEntity<Object> sendDeactivateEmail(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            if (log.isErrorEnabled()) {
                log.error("User not found with username: {}", username);
            }
            return new UserNotFoundException("User not found!");
        });
        if (user.getIsActive()) {
            if (sendEmailService.sendDeactivateEmailToUser(username)) {
                if (log.isInfoEnabled()) {
                    log.info("Deactivation email successfully sent to user with username: {}", username);
                }
                return new ResponseEntity<>("Email send", HttpStatus.OK);
            }
            if (log.isErrorEnabled()) {
                log.error("Failed to send deactivation email to user with username: {}", username);
            }
            throw new EmailFailedException("Sending email failed");
        }
        if (log.isInfoEnabled()) {
            log.info("User with username: {} is already deactivated", username);
        }
        throw new UserAlreadyDeactivatedException("User already deactivate exception");
    }

    @Override
    public ResponseEntity<Object> sendActivateEmail(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            if (log.isErrorEnabled()) {
                log.error("User not found with username: {}", username);
            }
            return new UserNotFoundException("User not found!");
        });
        if (!user.getIsActive()) {
            if (sendEmailService.sendActivateEmailToUser(username)) {
                if (log.isInfoEnabled()) {
                    log.info("Activation email successfully sent to user with username: {}", username);
                }
                return new ResponseEntity<>("Email send", HttpStatus.OK);
            }
            if (log.isErrorEnabled()) {
                log.error("Failed to send activation email to user with username: {}", username);
            }
            throw new EmailFailedException("Sending email failed");
        }
        if (log.isErrorEnabled()) {
            log.error("User with username: {} is already activated", username);
        }
        throw new UserAlreadyActivatedException("User already activate exception");
    }

    @Override
    public Boolean isFirstLogin(String usernameOrEmail) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> {
                    if (log.isErrorEnabled()) {
                        log.error("User not found with username or email: {}", usernameOrEmail);
                    }
                    return new UserNotFoundException("User not found!");
                });

        Boolean isFirstLogin = user.getIsFirstLogin();

        if (log.isInfoEnabled()) {
            log.info("User with username or email: {} first login status: {}", usernameOrEmail, isFirstLogin);
        }

        return isFirstLogin;
    }

    @Override
    public void abortChangePassword(ChangePasswordDTO changePasswordDTO) {
        User user = userRepository.findByUsername(changePasswordDTO.getUsername())
                .orElseThrow(() -> {
                    if (log.isErrorEnabled()) {
                        log.error("User not found with username: {}", changePasswordDTO.getUsername());
                    }
                    return new UserNotFoundException("User not found!");
                });

        user.setIsFirstLogin(false);
        userRepository.save(user);

        if (log.isInfoEnabled()) {
            log.info("Password change aborted for user with username: {}", changePasswordDTO.getUsername());
        }
    }

    @Override
    public ImageDTO getAvatarByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (user.getImage() == null) {
            throw new AvatarNotFoundException("Avatar not found!");
        }
        return user.getImage();
    }
}
