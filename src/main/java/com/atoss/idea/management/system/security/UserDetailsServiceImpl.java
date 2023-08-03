package com.atoss.idea.management.system.security;

import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.UserRegisterDTO;
import com.atoss.idea.management.system.repository.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructor for the UserDetailsServiceImpl class.
     *
     * @param userRepository The UserRepository used for accessing user-related data and operations.
     *
     * @see UserRepository
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user's details based on the provided username.
     *
     * @param username The username of the user whose details need to be loaded.
     *
     * @return A UserDetails object containing the user-specific details required for authentication and authorization.
     * @throws UsernameNotFoundException If a user with the given username is not found in the database.
     *
     * @see UserDetails
     * @see UserRegisterDTO
     * @see UserRepository
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserRegisterDTO.build(user);
    }
}
