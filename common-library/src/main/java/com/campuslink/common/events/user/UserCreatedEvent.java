package com.campuslink.common.events.user;

public record UserCreatedEvent(
        Long userId,
        String name,
        String email
) {
}
