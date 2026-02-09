package com.example.invitation_service.client;



import com.campuslink.common.dtos.responses.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name = "user-service",path = "/user")
public interface UserServiceClient {

    @GetMapping("/getUserById/{id}")
    public UserResponse getUserById(@PathVariable("id") Long id);

    @PostMapping("/users/batch")
    List<UserResponse> getUsersByIds(@RequestBody List<Long> ids);

}
