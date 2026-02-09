package com.CampusLink.mapper;

import com.CampusLink.entity.Club;
import com.campuslink.common.dtos.responses.UserResponse;
import com.campuslink.common.events.club.MemberJoinedClubEvent;
import org.apache.catalina.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ClubMapper {


    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.username", target = "userName")
    @Mapping(source = "user.email", target = "toEmail")
    @Mapping(source = "club.name", target = "clubName")
    MemberJoinedClubEvent toMemberJoinedClubEvent(UserResponse user, Club club);

}
