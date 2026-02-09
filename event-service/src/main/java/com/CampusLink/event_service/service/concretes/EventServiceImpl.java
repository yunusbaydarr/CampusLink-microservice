package com.CampusLink.event_service.service.concretes;


import com.CampusLink.event_service.client.UserServiceClient;
import com.CampusLink.event_service.client.ClubServiceClient;
import com.CampusLink.event_service.entity.Event;
import com.CampusLink.event_service.entity.EventParticipant;
import com.CampusLink.event_service.mapper.EventMapper;
import com.CampusLink.event_service.repository.EventParticipantRepository;
import com.CampusLink.event_service.repository.EventRepository;
import com.CampusLink.event_service.service.abstracts.EventService;
import com.campuslink.common.cloudinary.CloudinaryService;
import com.campuslink.common.dtos.requests.EventCreateRequest;
import com.campuslink.common.dtos.requests.EventUpdateRequest;
import com.campuslink.common.dtos.responses.GetEventParticipateResponse;
import com.campuslink.common.dtos.responses.GetEventResponse;
import com.campuslink.common.dtos.responses.UserResponse;
import com.campuslink.common.events.event.EventParticipatedEvent;
import com.campuslink.common.exceptions.ExceptionBuilder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    public static final String EVENT_EVENTS_TOPIC = "event-events-topic";

    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final UserServiceClient userServiceClient;
    private final ClubServiceClient clubServiceClient;
    private final KafkaTemplate<String,Object> kafkaTemplate;
    private final CloudinaryService cloudinaryService;


    private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

    private Event getEventOrThrow(Long eventId){
        return eventRepository.findById(eventId)
                .orElseThrow(()-> ExceptionBuilder.eventNotFound(eventId));
    }
    @Override
//    @Cacheable(
//            value = "event",
//            key = "#id"
//    )
    public Optional<GetEventResponse> getEventById(Long id) {

        Event event = getEventOrThrow(id);
/*
        GetEventResponse getEventResponse = new GetEventResponse();

        getEventResponse.setId(event.getId());
        getEventResponse.setDate(event.getDateTime());
        getEventResponse.setTitle(event.getTitle());
        getEventResponse.setDescription(event.getDescription());
       // getEventResponse.setCreatedByUserName(event.getCreatedBy().getName());
        getEventResponse.setCreatedByUserId(event.getCreatedByUserId());
        //getEventResponse.setClubName(event.getClub().getName());
        getEventResponse.setClubId(event.getClubId());
//        List<Long>  participantIds = new ArrayList<>();
//        participantIds.add(event.getCreatedByUserId());
//        getEventResponse.setParticipantUserIds(participantIds);

        List<Long> participantUserIds =event.getParticipants() != null
                ? event.getParticipants().stream().map(EventParticipant::getUserId).toList()
                :new ArrayList<>();

        getEventResponse.setParticipantUserIds(participantUserIds);

        return Optional.of(getEventResponse);

        */
      //  return eventRepository.findById(event.getId()).stream().map(this::convertToDto).orE;

        return eventRepository.findById(event.getId()).map(this::convertToDto);
    }

    private GetEventResponse convertToDto(Event event){
        GetEventResponse response = new GetEventResponse();

        response.setId(event.getId());
        response.setTitle(event.getTitle());
        response.setDescription(event.getDescription());
        response.setLocation(event.getLocation());
        response.setDate(event.getDateTime());
        response.setCreatedByUserId(event.getCreatedByUserId());
        response.setClubId(event.getClubId());
        response.setEventPictureUrl(event.getEventPictureUrl());

        List<Long> participantUserIds =event.getParticipants() != null
                ? event.getParticipants().stream().map(EventParticipant::getUserId).toList()
                :new ArrayList<>();

        response.setParticipantUserIds(participantUserIds);

        return response;

    }
    @Override
//    @Cacheable(value = "events_all")
    public List<GetEventResponse> getAllEvents() {

        return eventRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
//    @CacheEvict(
//            value = {
//                    "event",
//                    "events_all",
//                    "events_by_club",
//                    "events_by_user"
//            },
//            allEntries = true
//    )
    public GetEventResponse updateEvent(EventUpdateRequest updateRequest) {

        Event event = getEventOrThrow(updateRequest.getId());

        event.setId(updateRequest.getId());
        event.setTitle(updateRequest.getTitle());
        event.setDescription(updateRequest.getDescription());
        event.setLocation(updateRequest.getLocation());
        event.setDateTime(updateRequest.getDate());

        eventRepository.save(event);

        GetEventResponse response = new GetEventResponse();
        response.setId(event.getId());
        response.setTitle(event.getTitle());
        response.setDescription(event.getDescription());
        response.setLocation(event.getLocation());
        response.setDate(event.getDateTime());
        response.setCreatedByUserId(event.getCreatedByUserId());
        response.setClubId(event.getClubId());

        List<Long> participantIds = new ArrayList<>();
        participantIds.add(event.getCreatedByUserId());
        response.setParticipantUserIds(participantIds);

        return response;
    }

    @Override
//    @CacheEvict(
//            value = {
//                    "event",
//                    "events_all",
//                    "events_by_club",
//                    "events_by_user"
//            },
//            allEntries = true
//    )
    public void deleteEvent(Long id) {
        Event event =getEventOrThrow(id);

        eventRepository.delete(event);
    }

    @Override
    @Transactional
//    @CacheEvict(
//            value = {
//                    "event",
//                    "events_all",
//                    "events_by_club",
//                    "events_by_user"
//            },
//            allEntries = true
//    )
    public GetEventParticipateResponse participateEvent(Long eventId, Long userId) {
        Event event = getEventOrThrow(eventId);

        if (eventParticipantRepository.existsByEvent_IdAndUserId(eventId, userId)) {
            throw ExceptionBuilder.alreadyJoinedEvent(eventId, userId);
        }

      UserResponse user = userServiceClient.getUserById(userId);


        EventParticipant participant = new EventParticipant();
        participant.setEvent(event);
        participant.setUserId(userId);
        participant.setJoinedAt(new Date());

        try {
            eventParticipantRepository.save(participant);
        } catch (DataIntegrityViolationException ex) {
            throw ExceptionBuilder.alreadyJoinedEvent(eventId, userId);
        }

        GetEventParticipateResponse eventParticipant = new GetEventParticipateResponse();
        eventParticipant.setId(participant.getId());
        eventParticipant.setEventTitle(participant.getEvent().getTitle());
        eventParticipant.setUserId(participant.getUserId());
        eventParticipant.setJoinedAt(participant.getJoinedAt());



        EventParticipatedEvent eventParticipatedEvent = eventMapper.toEventParticipatedEvent(user,event);
        kafkaTemplate.send(EVENT_EVENTS_TOPIC,eventParticipatedEvent);

        return eventParticipant;
    }

    @Override
//    @CacheEvict(
//            value = {
//                    "event",
//                    "events_all",
//                    "events_by_club",
//                    "events_by_user"
//            },
//            allEntries = true
//    )
    public void cancelParticipation(Long eventId, Long userId) {

        Event event = getEventOrThrow(eventId);

        userServiceClient.getUserById(userId);


        EventParticipant eventParticipant = eventParticipantRepository.findByEventAndUserId(event, userId)
                .orElseThrow(() -> ExceptionBuilder.eventParticipantNotFound(eventId,userId));

        this.eventParticipantRepository.delete(eventParticipant);

    }

    @Override
//    @Cacheable(
//            value = "events_by_user",
//            key = "#userId"
//    )
    public List<GetEventResponse> getEventsByUserId(Long userId) {
        userServiceClient.getUserById(userId);

        List<Event> allEventsByUserId = eventRepository.findAllByCreatedByUserId(userId);

        List<GetEventResponse> responseList = allEventsByUserId.stream()
                .map(this::convertToDto)
                .toList();

        log.info("getEventsByUserId metodu başarıyla tamamlandı. {} yanıt döndürüldü.", responseList.size());

        return responseList;
    }

    @Override
    public List<Long> getParticipants(Long eventId) {
        Event event = getEventOrThrow(eventId);


        return event.getParticipants().stream()
                .map(EventParticipant::getUserId)
                .toList();
    }

    @Override
//    @CacheEvict(
//            value = {
//                    "events_all",
//                    "events_by_club",
//                    "events_by_user"
//            },
//            allEntries = true
//    )
    public GetEventResponse createEvent(Long clubId, Long userId, EventCreateRequest createRequest, MultipartFile image) {

        clubServiceClient.getClubById(clubId);
        userServiceClient.getUserById(userId);


        Event event = new Event();
        event.setClubId(clubId);
        event.setCreatedByUserId(userId);
        event.setTitle(createRequest.getTitle());
        event.setDescription(createRequest.getDescription());
        event.setLocation(createRequest.getLocation());
        event.setDateTime(createRequest.getDate());
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = cloudinaryService.uploadFile(image); // CloudinaryService'i buraya da inject etmelisin
        } else {
            imageUrl = "https://res.cloudinary.com/dejjmja6u/image/upload/v1769256254/default_event_photo.png";
        }
        event.setEventPictureUrl(imageUrl);

        eventRepository.save(event);

        GetEventResponse getEventResponse = new GetEventResponse();
        getEventResponse.setId(event.getId());
        getEventResponse.setDescription(event.getDescription());
        getEventResponse.setLocation(event.getLocation());
        getEventResponse.setTitle(event.getTitle());
        getEventResponse.setCreatedByUserId(event.getCreatedByUserId());
        getEventResponse.setDate(event.getDateTime());
        getEventResponse.setClubId(event.getClubId());
        getEventResponse.setEventPictureUrl(event.getEventPictureUrl());


        List<Long> participantId =  event.getParticipants()!=null
                ?
                event.getParticipants().stream()
                        .map(EventParticipant::getUserId)
                        .toList()
                : new ArrayList<>();
        getEventResponse.setParticipantUserIds(participantId);

        return getEventResponse;
    }

    @Override
//    @Cacheable(
//            value = "events_by_club",
//            key = "#clubId"
//    )
    public List<GetEventResponse> getEventsByClubId(Long clubId) {

        clubServiceClient.getClubById(clubId);

        List<Event> events = eventRepository.findAllByClubId(clubId);
        return events.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
