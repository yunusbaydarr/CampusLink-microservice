package com.example.invitation_service.mapper;


import com.campuslink.common.dtos.responses.GetClubResponse;
import com.campuslink.common.dtos.responses.UserResponse;
import com.campuslink.common.events.invitation.InvitationSentEvent;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface InvitationMapper {

    @Mapping(source = "toUser.id", target = "toUserId")
    @Mapping(source = "toUser.username", target = "toUserName")
    @Mapping(source = "toUser.email", target = "toUserEmail")

    @Mapping(source = "club.name", target = "clubName")

    @Mapping(source = "fromUser.username", target = "fromUserName")
    InvitationSentEvent toInvitationSentEvent(UserResponse toUser, UserResponse fromUser, GetClubResponse club);



}
