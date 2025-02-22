package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private UserController userController;

    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = TestUtils.createTestUser();
    }

    @Test
    void testFindById_UserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<User> response = userController.findById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("testUser", response.getBody().getUsername());
    }

    @Test
    void testFindById_UserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.findById(2L);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testFindByUserName_UserExists() {
        when(userRepository.findByUsername("testUser")).thenReturn(testUser);

        ResponseEntity<User> response = userController.findByUserName("testUser");

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("testUser", response.getBody().getUsername());
    }

    @Test
    void testFindByUserName_UserNotFound() {
        when(userRepository.findByUsername("unknownUser")).thenReturn(null);

        ResponseEntity<User> response = userController.findByUserName("unknownUser");

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testCreateUser_Success() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newUser");

        when(cartRepository.save(any(Cart.class))).thenReturn(new Cart());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<User> response = userController.createUser(request);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("newUser", response.getBody().getUsername());

        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(userRepository, times(1)).save(any(User.class));
    }
}
