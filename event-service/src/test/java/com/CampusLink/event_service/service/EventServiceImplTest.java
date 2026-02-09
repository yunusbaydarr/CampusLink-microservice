package com.CampusLink.event_service.service;

import com.CampusLink.event_service.client.ClubServiceClient;
import com.CampusLink.event_service.client.UserServiceClient;
import com.CampusLink.event_service.entity.Event;
import com.CampusLink.event_service.entity.EventParticipant;
import com.CampusLink.event_service.mapper.EventMapper;
import com.CampusLink.event_service.repository.EventParticipantRepository;
import com.CampusLink.event_service.repository.EventRepository;
import com.CampusLink.event_service.service.concretes.EventServiceImpl;
import com.campuslink.common.dtos.requests.EventCreateRequest;
import com.campuslink.common.dtos.requests.EventUpdateRequest;
import com.campuslink.common.dtos.responses.GetEventParticipateResponse;
import com.campuslink.common.dtos.responses.GetEventResponse;
import com.campuslink.common.dtos.responses.UserResponse;
import com.campuslink.common.events.event.EventParticipatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock private EventMapper eventMapper;
    @Mock private EventRepository eventRepository;
    @Mock private EventParticipantRepository eventParticipantRepository;
    @Mock private UserServiceClient userServiceClient;
    @Mock private ClubServiceClient clubServiceClient;
    @Mock private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    @DisplayName("Create Event - Başarılı")
    void createEvent_shouldSaveAndReturnResponse() {
        Long clubId = 1L;
        Long userId = 2L;
        EventCreateRequest request = new EventCreateRequest();
        request.setTitle("Parti");
        request.setDescription("Eğlence");
        request.setLocation("Kampüs");
        request.setClubPictureUrl("img.jpg");

        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> {
            Event e = inv.getArgument(0);
            e.setId(100L);
            return e;
        });

        GetEventResponse response = eventService.createEvent(clubId, userId, request, null);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Parti", response.getTitle());
        verify(clubServiceClient).getClubById(clubId);
        verify(userServiceClient).getUserById(userId);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    @DisplayName("Participate Event - Başarılı Katılım")
    void participateEvent_shouldSaveParticipantAndSendKafka() {
        Long eventId = 10L;
        Long userId = 20L;

        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Konferans");

        UserResponse user = new UserResponse();
        user.setId(userId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventParticipantRepository.existsByEvent_IdAndUserId(eventId, userId)).thenReturn(false);
        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(eventMapper.toEventParticipatedEvent(user, event)).thenReturn(new EventParticipatedEvent(
                userId,
                "Deneme User",
                "deneme@test.com",
                "Deneme Event Title"

        ));

        GetEventParticipateResponse response = eventService.participateEvent(eventId, userId);

        assertNotNull(response);
        verify(eventParticipantRepository).save(any(EventParticipant.class));
        verify(kafkaTemplate).send(eq(EventServiceImpl.EVENT_EVENTS_TOPIC), any(EventParticipatedEvent.class));
    }

    @Test
    @DisplayName("Participate Event - Zaten Katılmışsa Hata")
    void participateEvent_shouldThrowException_whenAlreadyJoined() {
        Long eventId = 10L;
        Long userId = 20L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(new Event()));
        when(eventParticipantRepository.existsByEvent_IdAndUserId(eventId, userId)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> eventService.participateEvent(eventId, userId));
        verify(eventParticipantRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update Event - Başarılı")
    void updateEvent_shouldUpdateFields() {
        Long eventId = 1L;
        EventUpdateRequest request = new EventUpdateRequest();
        request.setId(eventId);
        request.setTitle("Yeni Başlık");

        Event existingEvent = new Event();
        existingEvent.setId(eventId);
        existingEvent.setTitle("Eski Başlık");

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));

        GetEventResponse response = eventService.updateEvent(request);

        assertEquals("Yeni Başlık", response.getTitle());
        verify(eventRepository).save(existingEvent);
    }

    @Test
    @DisplayName("Delete Event - Başarılı")
    void deleteEvent_shouldCallRepositoryDelete() {
        Long eventId = 5L;
        Event event = new Event();
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        eventService.deleteEvent(eventId);

        verify(eventRepository).delete(event);
    }
}
