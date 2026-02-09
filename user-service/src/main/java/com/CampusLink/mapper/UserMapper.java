package com.CampusLink.mapper;

import com.CampusLink.entity.User;
import com.campuslink.common.dtos.requests.*;
import com.campuslink.common.dtos.responses.AuthResponse;
import com.campuslink.common.dtos.responses.GetUserResponse;
import com.campuslink.common.dtos.responses.UserResponse;
import com.campuslink.common.events.user.UserCreatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface UserMapper {
//    User toEntity(UserCreateRequest request);
//    User toEntity(UserUpdateRequest request);
//    User toEntity(AuthRequest request);
//    User toEntity(RegisterRequest request);
//    User toEntity(TokenRefreshRequest request);
//
//    UserResponse toResponse(User user);
//    GetUserResponse toGetUserResponse(User user);
//    AuthResponse toAuthResponse(User user);
//
//    UserCreatedEvent toUserCreatedEvent(User user);

    @Mapping(source = "id", target = "userId")
    UserCreatedEvent toUserCreatedEvent (User user);

}
