package com.example.notification_service.client.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponse {
    private Long id;
    private String name;
    private String username;
    private String role;
    private String profilePictureUrl;

    public UserResponse(Long userId) {
        this.id=userId;
    }
}
