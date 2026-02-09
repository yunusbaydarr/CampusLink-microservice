package com.campuslink.common.events.club;

public record MemberJoinedClubEvent(
        Long userId,
        String name ,
        String toEmail,
        String userName,
        String clubName) {
}
