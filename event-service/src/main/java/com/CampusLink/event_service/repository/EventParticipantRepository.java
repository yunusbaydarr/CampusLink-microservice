package com.CampusLink.event_service.repository;


import com.CampusLink.event_service.entity.Event;
import com.CampusLink.event_service.entity.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant,Long> {

    @Query("""
        SELECT ep
        FROM EventParticipant ep
        WHERE ep.event = :event
          AND ep.userId = :userId
    """)
    Optional<EventParticipant> findByEventAndUserId(
            @Param("event") Event event,
            @Param("userId") Long userId
    );

    @Query("""
        SELECT ep
        FROM EventParticipant ep
        WHERE ep.event.id = :eventId
    """)
    List<EventParticipant> findAllByEventId(@Param("eventId") Long eventId);

    boolean existsByEvent_IdAndUserId(Long eventId, Long userId);

}
