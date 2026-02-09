package com.campuslink.common.dtos.requests;


import com.campuslink.common.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequest {
    private String name;
    private String username;
    private String email;
    private String password;
    private UserRole role;
}
