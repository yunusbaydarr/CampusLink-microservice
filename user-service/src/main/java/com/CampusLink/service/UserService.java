package com.CampusLink.service;




import com.campuslink.common.dtos.requests.UserCreateRequest;
import com.campuslink.common.dtos.requests.UserUpdateRequest;
import com.campuslink.common.dtos.responses.GetUserResponse;
import com.campuslink.common.dtos.responses.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {
    GetUserResponse createUser (UserCreateRequest createRequest, MultipartFile file);
    Optional<UserResponse> getUserById(Long id);
    List<GetUserResponse> getAllUsers();
    GetUserResponse updateUser(UserUpdateRequest updateRequest);
    void deleteUser(Long id);
    public List<GetUserResponse> getUsersByIds(List<Long> ids);

}
