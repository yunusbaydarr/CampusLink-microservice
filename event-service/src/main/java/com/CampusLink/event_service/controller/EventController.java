package com.CampusLink.event_service.controller;


import com.CampusLink.event_service.service.abstracts.EventService;
import com.campuslink.common.dtos.requests.EventCreateRequest;
import com.campuslink.common.dtos.requests.EventUpdateRequest;
import com.campuslink.common.dtos.responses.GetEventParticipateResponse;
import com.campuslink.common.dtos.responses.GetEventResponse;
import com.campuslink.common.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;


    @GetMapping("/getEventById/{id}")
    public ResponseEntity<GetEventResponse> getEventById(@PathVariable Long id) {
        Optional<GetEventResponse> eventById = this.eventService.getEventById(id);

        return eventById.map(ResponseEntity::ok)
                .orElseGet(()-> ResponseEntity.notFound().build());
    }

    @GetMapping("/getAllEvents")
    public ResponseEntity<List<GetEventResponse>> getAllEvents() {
        List<GetEventResponse> allEvents = this.eventService.getAllEvents();

        return ResponseEntity.ok(allEvents);
    }

    @PutMapping("/updateEvent")
    public ResponseEntity<GetEventResponse> updateEvent(@RequestBody EventUpdateRequest updateRequest) {
        GetEventResponse getEventResponse = this.eventService.updateEvent(updateRequest);
        return ResponseEntity.ok(getEventResponse);
    }

    @DeleteMapping("/deleteEvent/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        this.eventService.deleteEvent(id);

        return ResponseEntity.noContent().build();

    }

    @PostMapping("/participateEvent/{eventId}")
    public ResponseEntity<GetEventParticipateResponse> participateEvent(@PathVariable Long eventId,
                                                                        @AuthenticationPrincipal AuthenticatedUser user) {
        GetEventParticipateResponse getEventParticipateResponse = this.eventService.participateEvent(eventId, user.getId());

        return ResponseEntity.ok(getEventParticipateResponse);
    }

    @DeleteMapping("/cancelParticipation/{eventId}")
    public ResponseEntity<Void> cancelParticipation(@PathVariable Long eventId , @AuthenticationPrincipal AuthenticatedUser user) {
        this.eventService.cancelParticipation(eventId,user.getId());
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/getEventsByUserId/{userId}")
//    public ResponseEntity<List<GetEventResponse>> getEventsByUserId(@PathVariable Long userId) {
//        List<GetEventResponse> eventsByUserId = this.eventService.getEventsByUserId(userId);
//        return ResponseEntity.ok(eventsByUserId);
//    }
    @GetMapping("/getEventsByUserId")
    public ResponseEntity<List<GetEventResponse>> getEventsByUserId(@AuthenticationPrincipal AuthenticatedUser user) {
        List<GetEventResponse> eventsByUserId = this.eventService.getEventsByUserId(user.getId());
        return ResponseEntity.ok(eventsByUserId);
    }


    @GetMapping("/getParticipants/{eventId}")
    public ResponseEntity<List<Long>> getParticipants(@PathVariable Long eventId) {
        List<Long> participants = this.eventService.getParticipants(eventId);
        return ResponseEntity.ok(participants);
    }

//    @PostMapping("/createEvent/{clubId}")
//    public ResponseEntity<GetEventResponse> createEvent(@PathVariable Long clubId,
//                                                        @RequestBody EventCreateRequest createRequest,
//                                                        @AuthenticationPrincipal AuthenticatedUser user) {
//
//        GetEventResponse event = this.eventService.createEvent(clubId, user.getId(), createRequest);
//        return ResponseEntity.ok(event);
//    }
@PostMapping(
        value = "/createEvent/{clubId}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
)
public ResponseEntity<GetEventResponse> createEvent(
        @PathVariable Long clubId,
        @RequestPart("event") EventCreateRequest createRequest,
        @RequestPart(value = "image", required = false) MultipartFile image,
        @AuthenticationPrincipal AuthenticatedUser user
) {

    GetEventResponse event =
            this.eventService.createEvent(clubId, user.getId(), createRequest, image);

    return ResponseEntity.ok(event);
}

    @GetMapping("/getEventsByClubId/{clubId}")
    public ResponseEntity<List<GetEventResponse>> getEventsByClubId(@PathVariable Long clubId) {

        List<GetEventResponse> eventsByClubId = this.eventService.getEventsByClubId(clubId);

        return ResponseEntity.ok(eventsByClubId);
    }
}
