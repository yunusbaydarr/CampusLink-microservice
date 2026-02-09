package com.example.notification_service.client;


import com.campuslink.common.dtos.responses.GetClubResponse;
import com.campuslink.common.dtos.responses.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "club-service",path = "/club")
public interface ClubServiceClient {

    @GetMapping("/getClubById/{id}")
    public GetClubResponse getClubById(@PathVariable("id") Long id);

}
