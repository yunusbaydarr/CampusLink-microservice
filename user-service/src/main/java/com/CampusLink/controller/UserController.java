package com.CampusLink.controller;



import com.CampusLink.security.SecurityUser;
import com.CampusLink.service.UserService;
import com.campuslink.common.dtos.requests.UserCreateRequest;
import com.campuslink.common.dtos.requests.UserUpdateRequest;
import com.campuslink.common.dtos.responses.GetUserResponse;
import com.campuslink.common.dtos.responses.UserResponse;
import com.campuslink.common.security.AuthenticatedUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/createUser", consumes = "multipart/form-data")
    public ResponseEntity<GetUserResponse> createUser(
            @RequestPart("user") UserCreateRequest createRequest,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        log.info(">>> /user/createUser called! body: {}", createRequest);
        GetUserResponse getUserResponse = this.userService.createUser(createRequest,file);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(getUserResponse.getId())
                .toUri();

        return ResponseEntity.created(location).body(getUserResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(userService.getUserById(user.getId()).orElseThrow());
    }


    @GetMapping("/getUserById/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        Optional<UserResponse> user = this.userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/getAllUser")
    public ResponseEntity<List<GetUserResponse>> getAllUsers() {
        List<GetUserResponse> users = this.userService.getAllUsers();

        if (users.isEmpty()){
           return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);

    }

    @PutMapping("/updateUser")
    public ResponseEntity<GetUserResponse> updateUser(     @RequestBody UserUpdateRequest updateRequest,
                                                           @AuthenticationPrincipal AuthenticatedUser user) {

        if (user == null) {
            throw new RuntimeException("Kullanıcı doğrulanamadı. Lütfen geçerli bir Token ile istek atın.");
        }

        log.info("Update isteği geldi. User ID: {}", user.getId());

        updateRequest.setId(user.getId());
        GetUserResponse userResponse = this.userService.updateUser(updateRequest);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/users/batch")
    public List<GetUserResponse> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }
}
