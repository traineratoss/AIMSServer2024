package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Role;
import com.atoss.idea.management.system.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameOrEmail(String username, String email);

    Optional<User> findByUsername(String username);

    // FlorinCP : added to ensure better functionality to a method ,
    //              the basic one , findById was troublesome
    //              + findUserByUsername
    User findUserById(Long userId);

    User findUserByUsername(String username);

    List<User> findUserByRole(Role role);

    Optional<User> findByEmail(String email);

    List<User> findByIsActive(boolean isActive);

    List<User> findByUsernameStartsWith(String username);
}
