package com.CampusLink.service;


import com.CampusLink.entity.User;
import com.CampusLink.mapper.UserMapper;
import com.CampusLink.repository.UserRepository;
import com.campuslink.common.cloudinary.CloudinaryService;
import com.campuslink.common.dtos.requests.UserCreateRequest;
import com.campuslink.common.dtos.requests.UserUpdateRequest;
import com.campuslink.common.dtos.responses.GetUserResponse;
import com.campuslink.common.dtos.responses.UserResponse;
import com.campuslink.common.events.user.UserCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserMapper userMapper;
    @Mock private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock private UserRepository userRepository;
    @Mock private CloudinaryService cloudinaryService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Create User - Başarılı")
    void createUser_shouldSaveAndSendKafka() {
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("test@test.com");
        request.setUsername("testuser");
        request.setPassword("123456");
        request.setName("Test Name");

        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes());

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPass");
        when(cloudinaryService.uploadFile(any(MultipartFile.class))).thenReturn("http://url.com");

        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(55L);
            return u;
        });

        when(userMapper.toUserCreatedEvent(any(User.class))).thenReturn(new UserCreatedEvent(
                1L,
                "Denem User",
                "deneme@test.com"
        ));

        GetUserResponse response = userService.createUser(request, file);

        assertNotNull(response);
        assertEquals(55L, response.getId());
        assertEquals("http://url.com", response.getProfilePictureUrl());
        verify(kafkaTemplate).send(eq(UserServiceImpl.USER_EVENTS_TOPIC), any(UserCreatedEvent.class));
    }

    @Test
    @DisplayName("Get User By ID - Başarılı")
    void getUserById_shouldReturnUser() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Ali");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<UserResponse> result = userService.getUserById(userId);

        assertTrue(result.isPresent());
        assertEquals("Ali", result.get().getName());
    }

    @Test
    @DisplayName("Update User - Başarılı")
    void updateUser_shouldUpdateFields() {
        Long userId = 1L;
        UserUpdateRequest request = new UserUpdateRequest();
        request.setId(userId);
        request.setName("New Name");
        request.setPassword("newPass");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        GetUserResponse response = userService.updateUser(request);

        assertEquals("New Name", response.getName());
        verify(userRepository).save(existingUser);
    }
}