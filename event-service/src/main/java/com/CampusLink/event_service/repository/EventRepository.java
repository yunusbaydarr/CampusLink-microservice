package com.CampusLink.event_service.repository;


import com.CampusLink.event_service.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event,Long> {

    @Query("""
        SELECT e
        FROM Event e
        WHERE e.clubId = :clubId
    """)
    List<Event> findAllByClubId(@Param("clubId") Long clubId);



    @Query("""
        SELECT e
        FROM Event e
        WHERE e.createdByUserId = :userId
    """)
    List<Event> findAllByCreatedByUserId(@Param("userId") Long userId);



    @Query("""
        SELECT e
        FROM Event e
        WHERE e.createdByUserId = :userId
    """)
    List<Event> findEventsByCreatorUserId(@Param("userId") Long userId);
}
