package com.CampusLink.event_service.service.abstracts;



import com.campuslink.common.dtos.requests.EventCreateRequest;
import com.campuslink.common.dtos.requests.EventUpdateRequest;
import com.campuslink.common.dtos.responses.GetEventParticipateResponse;
import com.campuslink.common.dtos.responses.GetEventResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface EventService {

    Optional<GetEventResponse> getEventById(Long id);

    List<GetEventResponse> getAllEvents();

    GetEventResponse updateEvent(EventUpdateRequest updateRequest);

    void deleteEvent(Long id);

    GetEventParticipateResponse participateEvent(Long eventId, Long userId);

    void cancelParticipation(Long eventId, Long userId);

    List<GetEventResponse> getEventsByUserId(Long userId);

    List<Long> getParticipants(Long eventId);

    GetEventResponse createEvent(Long clubId, Long userId, EventCreateRequest createRequest, MultipartFile image);

    List<GetEventResponse> getEventsByClubId(Long clubId);




}
