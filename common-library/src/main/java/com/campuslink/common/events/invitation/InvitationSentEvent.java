package com.campuslink.common.events.invitation;

public record InvitationSentEvent(

        Long toUserId,
        String toUserName,
        String toUserEmail,
        String clubName,
        String fromUserName
) {
}
