package com.example.hotelbooking.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.hotelbooking.model.User;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByEmail() {
        // Given
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setUsername("johndoe");
        user.setPassword("password123");
        user.setRole("USER");
        user.setEnabled(true);
        
        User savedUser = entityManager.persistAndFlush(user);

        // When
        Optional<User> foundUser = userRepository.findByEmail("john@example.com");

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("John Doe", foundUser.get().getName());
        assertEquals("john@example.com", foundUser.get().getEmail());
        assertEquals("johndoe", foundUser.get().getUsername());
    }

    @Test
    void testFindByUsername() {
        // Given
        User user = new User();
        user.setName("Jane Smith");
        user.setEmail("jane@example.com");
        user.setUsername("janesmith");
        user.setPassword("password123");
        user.setRole("USER");
        user.setEnabled(true);
        
        User savedUser = entityManager.persistAndFlush(user);

        // When
        Optional<User> foundUser = userRepository.findByUsername("janesmith");

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("Jane Smith", foundUser.get().getName());
        assertEquals("jane@example.com", foundUser.get().getEmail());
        assertEquals("janesmith", foundUser.get().getUsername());
    }

    @Test
    void testFindByUsernameOrEmail() {
        // Given
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setRole("USER");
        user.setEnabled(true);
        
        User savedUser = entityManager.persistAndFlush(user);

        // When - Search by username
        Optional<User> foundByUsername = userRepository.findByUsernameOrEmail("testuser");

        // Then
        assertTrue(foundByUsername.isPresent());
        assertEquals("testuser", foundByUsername.get().getUsername());

        // When - Search by email
        Optional<User> foundByEmail = userRepository.findByUsernameOrEmail("test@example.com");

        // Then
        assertTrue(foundByEmail.isPresent());
        assertEquals("test@example.com", foundByEmail.get().getEmail());
    }

    @Test
    void testExistsByEmail() {
        // Given
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setUsername("johndoe");
        user.setPassword("password123");
        user.setRole("USER");
        user.setEnabled(true);
        
        entityManager.persistAndFlush(user);

        // When & Then
        assertTrue(userRepository.existsByEmail("john@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    void testExistsByUsername() {
        // Given
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setUsername("johndoe");
        user.setPassword("password123");
        user.setRole("USER");
        user.setEnabled(true);
        
        entityManager.persistAndFlush(user);

        // When & Then
        assertTrue(userRepository.existsByUsername("johndoe"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    void testSaveUser() {
        // Given
        User user = new User();
        user.setName("New User");
        user.setEmail("new@example.com");
        user.setUsername("newuser");
        user.setPassword("password123");
        user.setRole("USER");
        user.setEnabled(true);

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertNotNull(savedUser.getId());
        assertEquals("New User", savedUser.getName());
        assertEquals("new@example.com", savedUser.getEmail());
        assertEquals("newuser", savedUser.getUsername());
        assertTrue(savedUser.isEnabled());
    }

    @Test
    void testUpdateUser() {
        // Given
        User user = new User();
        user.setName("Original Name");
        user.setEmail("original@example.com");
        user.setUsername("originaluser");
        user.setPassword("password123");
        user.setRole("USER");
        user.setEnabled(true);
        
        User savedUser = entityManager.persistAndFlush(user);

        // When
        savedUser.setName("Updated Name");
        savedUser.setEmail("updated@example.com");
        User updatedUser = userRepository.save(savedUser);

        // Then
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals(savedUser.getId(), updatedUser.getId());
    }

    @Test
    void testDeleteUser() {
        // Given
        User user = new User();
        user.setName("User to Delete");
        user.setEmail("delete@example.com");
        user.setUsername("deleteuser");
        user.setPassword("password123");
        user.setRole("USER");
        user.setEnabled(true);
        
        User savedUser = entityManager.persistAndFlush(user);

        // When
        userRepository.deleteById(savedUser.getId());

        // Then
        assertFalse(userRepository.existsById(savedUser.getId()));
    }
}