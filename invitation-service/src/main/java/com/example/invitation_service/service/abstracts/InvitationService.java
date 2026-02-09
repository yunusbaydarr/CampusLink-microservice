package com.example.invitation_service.service.abstracts;

import com.campuslink.common.dtos.requests.InvitationCreateRequest;
import com.campuslink.common.dtos.requests.InvitationUpdateRequest;
import com.campuslink.common.dtos.responses.GetInvitationResponse;

import java.util.List;

public interface InvitationService {
    GetInvitationResponse sendInvitation(InvitationCreateRequest request);
    GetInvitationResponse respondToInvitation(Long invitationId, InvitationUpdateRequest request);
    List<GetInvitationResponse> getIncomingInvitations(Long userId);

    void expireOldInvitations();
}
