package com.campuslink.common.dtos.responses;



import com.campuslink.common.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserResponse {

    private Long id;
    private String name;
    private String username;
    private String email;
    private UserRole role;
    private String profilePictureUrl;




}
