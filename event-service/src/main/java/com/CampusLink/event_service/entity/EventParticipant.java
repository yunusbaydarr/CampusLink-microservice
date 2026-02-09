package com.CampusLink.event_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "event_participants",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"event_id", "user_id"})
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "user_id")
    private Long userId;

    private Date joinedAt;
}
