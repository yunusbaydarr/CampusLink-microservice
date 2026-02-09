package com.CampusLink.event_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String title;

        @Column(columnDefinition = "TEXT" , name = "description")
        private String description;
        private String location;
        private String eventPictureUrl;
        private LocalDateTime dateTime;

        @Column(name = "club_id",nullable = false)
        private Long clubId;

        @Column(name = "created_by_user_id",nullable = false)
        private Long createdByUserId;

        @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<EventParticipant> participants = new ArrayList<>();

}
