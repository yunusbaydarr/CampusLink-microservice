package com.CampusLink.service;



import com.CampusLink.entity.User;


import com.CampusLink.mapper.UserMapper;
import com.CampusLink.repository.UserRepository;
import com.campuslink.common.cloudinary.CloudinaryService;
import com.campuslink.common.dtos.requests.UserCreateRequest;
import com.campuslink.common.dtos.requests.UserUpdateRequest;
import com.campuslink.common.dtos.responses.GetUserResponse;
import com.campuslink.common.dtos.responses.UserResponse;
import com.campuslink.common.enums.UserRole;
import com.campuslink.common.events.user.UserCreatedEvent;
import com.campuslink.common.exceptions.ExceptionBuilder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    public static final String USER_EVENTS_TOPIC = "user-events-topic";

    private final UserMapper userMapper;
    private final KafkaTemplate<String,Object> kafkaTemplate;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, KafkaTemplate<String, Object> kafkaTemplate, UserRepository userRepository,
                           CloudinaryService cloudinaryService,
                           PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
        this.passwordEncoder=passwordEncoder;
    }

//    @CacheEvict(value = { "allUsers" }, allEntries = true)
    @Override
    public GetUserResponse createUser(UserCreateRequest createRequest, MultipartFile file) {

        if (userRepository.existsByEmail(createRequest.getEmail())){
            throw ExceptionBuilder.emailAlreadyExists(createRequest.getEmail());
        }

        if (userRepository.existsByUsername(createRequest.getUsername())){
            throw ExceptionBuilder.emailAlreadyExists(createRequest.getUsername());
        }

        User user = new User();
        user.setName(createRequest.getName());
        user.setEmail(createRequest.getEmail());
        user.setPassword(passwordEncoder.encode(createRequest.getPassword()));
        user.setRole(UserRole.STUDENT);
        user.setUsername(createRequest.getUsername());

        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            imageUrl = cloudinaryService.uploadFile(file);
            if (imageUrl == null) {
                throw ExceptionBuilder.imageUploadFailed();
            }
        } else {
            imageUrl = "https://res.cloudinary.com/dejjmja6u/image/upload/v1766611245/hand-drawn-question-mark-silhouette_23-2150940534_gmm2lh.avif";
        }
        user.setProfilePictureUrl(imageUrl);

        User savedUser = userRepository.save(user);

        UserCreatedEvent event = userMapper.toUserCreatedEvent(savedUser);


        kafkaTemplate.send(USER_EVENTS_TOPIC,event);


        GetUserResponse getUserResponse = new GetUserResponse();
        getUserResponse.setId(savedUser.getId());
        getUserResponse.setName(savedUser.getName());
        getUserResponse.setEmail(savedUser.getEmail());
        getUserResponse.setRole(savedUser.getRole());
        getUserResponse.setUsername(savedUser.getUsername());
        getUserResponse.setProfilePictureUrl(savedUser.getProfilePictureUrl());

        return getUserResponse;
    }

    @Override
    public Optional<UserResponse> getUserById(Long id)  {
            User userById = userRepository.findById(id)
                    .orElseThrow(() -> ExceptionBuilder.userNotFound(id));


                UserResponse userResponse = new UserResponse();
                userResponse.setId(userById.getId());
                userResponse.setName(userById.getName());
                userResponse.setEmail(userById.getEmail());
                userResponse.setRole(userById.getRole());
                userResponse.setUsername(userById.getUsername());
                userResponse.setProfilePictureUrl(userById.getProfilePictureUrl());


        return Optional.of(userResponse);
    }

//    @Cacheable(value = "allUsers")
    @Override
    public List<GetUserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

//    @CacheEvict(
//            value = { "users", "usersByIds", "allUsers" },
//            key = "#updateRequest.id",
//            allEntries = true
//    )
    @Override
    public GetUserResponse updateUser(UserUpdateRequest updateRequest) {

        User user = userRepository.findById(updateRequest.getId())
                .orElseThrow(() -> ExceptionBuilder.userNotFound(updateRequest.getId()));

        user.setName(updateRequest.getName());
        user.setEmail(updateRequest.getEmail());
        user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));

        userRepository.save(user);
        GetUserResponse userResponse = new GetUserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole());

        return userResponse;
    }

//    @CacheEvict(
//            value = { "users", "usersByIds", "allUsers" },
//            key = "#id",
//            allEntries = true
//    )
    @Override
    public void deleteUser(Long id) {
        User byId = userRepository.findById(id)
                .orElseThrow(()-> ExceptionBuilder.userNotFound(id));

        this.userRepository.delete(byId);

    }

    private GetUserResponse convertToDto(User user) {
        GetUserResponse userResponse = new GetUserResponse();

        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole());
        userResponse.setProfilePictureUrl(user.getProfilePictureUrl());
        return userResponse;
    }

    @Override
//    @Cacheable(value = "usersByIds", key = "#ids")
    public List<GetUserResponse> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);

        return users.stream()
                .map(this::convertToDto)
                .toList();
    }


}
