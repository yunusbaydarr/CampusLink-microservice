package com.CampusLink.event_service.mapper;


import com.CampusLink.event_service.entity.Event;
import com.campuslink.common.dtos.responses.UserResponse;
import com.campuslink.common.events.event.EventParticipatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface EventMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "userName")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "event.title", target = "eventTitle")
    EventParticipatedEvent toEventParticipatedEvent(UserResponse user, Event event);
}
