package com.campuslink.common.events.event;

public record EventParticipatedEvent(
        Long userId,
        String userName,
        String userEmail,
        String eventTitle
) {
}
