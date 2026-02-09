package com.example.invitation_service.controller;

import com.campuslink.common.dtos.requests.InvitationCreateRequest;
import com.campuslink.common.dtos.requests.InvitationUpdateRequest;
import com.campuslink.common.dtos.responses.GetInvitationResponse;
import com.campuslink.common.security.AuthenticatedUser;
import com.example.invitation_service.service.abstracts.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/invitation")
@RequiredArgsConstructor
public class InvitationController {

  private final InvitationService invitationService ;

    @PostMapping("/sendInvitation")
    public ResponseEntity<GetInvitationResponse> sendInvitation(@RequestBody InvitationCreateRequest request,
                                                                @AuthenticationPrincipal AuthenticatedUser user) {

        request.setFromUserId(user.getId());

        GetInvitationResponse response = this.invitationService.sendInvitation(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/respondToInvitation/{invitationId}")
    public ResponseEntity<GetInvitationResponse> respondToInvitation(@PathVariable Long invitationId, @RequestBody InvitationUpdateRequest request) {
        GetInvitationResponse getInvitationResponse = this.invitationService.respondToInvitation(invitationId, request);
        if (getInvitationResponse==null){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(getInvitationResponse);
    }

    @GetMapping("/getIncomingInvitations")
    public ResponseEntity<List<GetInvitationResponse>> getIncomingInvitations(@AuthenticationPrincipal AuthenticatedUser user) {
        List<GetInvitationResponse> incomingInvitations = this.invitationService.getIncomingInvitations(user.getId());

        if (incomingInvitations.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(incomingInvitations);
    }



    }
