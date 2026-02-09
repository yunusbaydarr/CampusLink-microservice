package com.example.invitation_service.service;

import com.campuslink.common.dtos.requests.InvitationCreateRequest;
import com.campuslink.common.dtos.requests.InvitationUpdateRequest;
import com.campuslink.common.dtos.responses.GetClubResponse;
import com.campuslink.common.dtos.responses.GetInvitationResponse;
import com.campuslink.common.dtos.responses.UserResponse;
import com.campuslink.common.enums.Status;
import com.campuslink.common.events.invitation.InvitationSentEvent;
import com.example.invitation_service.client.ClubServiceClient;
import com.example.invitation_service.client.UserServiceClient;
import com.example.invitation_service.entity.Invitation;
import com.example.invitation_service.mapper.InvitationMapper;
import com.example.invitation_service.repository.InvitationRepository;
import com.example.invitation_service.service.concretes.InvitationServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvitationServiceImplTest {

    @Mock private InvitationMapper invitationMapper;
    @Mock private InvitationRepository invitationRepository;
    @Mock private UserServiceClient userServiceClient;
    @Mock private ClubServiceClient clubServiceClient;
    @Mock private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private InvitationServiceImpl invitationService;

    @Test
    @DisplayName("Send Invitation - Başarılı")
    void sendInvitation_shouldSaveAndSendKafka() {
        InvitationCreateRequest request = new InvitationCreateRequest();
        request.setFromUserId(1L);
        request.setToUserId(2L);
        request.setClubId(10L);

        when(clubServiceClient.getClubById(10L)).thenReturn(new GetClubResponse());
        when(userServiceClient.getUserById(anyLong())).thenReturn(new UserResponse());
        when(invitationRepository.existsByClubAndToUserWithStatus(10L, 2L, Status.PENDING)).thenReturn(false);

        when(invitationRepository.save(any(Invitation.class))).thenAnswer(inv -> {
            Invitation i = inv.getArgument(0);
            i.setId(99L);
            return i;
        });

        when(invitationMapper.toInvitationSentEvent(any(), any(), any())).thenReturn(new InvitationSentEvent(
                request.getToUserId(),
                "DenemetoUserName",
                "deneme@test.com",
                "Deneme Club Name",
                "Deneme From User NAme"
        ));

        GetInvitationResponse response = invitationService.sendInvitation(request);

        assertNotNull(response);
        assertEquals(Status.PENDING, response.getStatus());
        verify(kafkaTemplate).send(eq(InvitationServiceImpl.INVITATION_EVENTS_TOPIC), any(InvitationSentEvent.class));
    }

    @Test
    @DisplayName("Respond To Invitation - Başarılı")
    void respondToInvitation_shouldUpdateStatus() {
        Long invId = 1L;
        InvitationUpdateRequest request = new InvitationUpdateRequest();
        request.setStatus(Status.ACCEPTED);

        Invitation invitation = new Invitation();
        invitation.setId(invId);
        invitation.setStatus(Status.PENDING);

        when(invitationRepository.findById(invId)).thenReturn(Optional.of(invitation));
        when(invitationRepository.save(any(Invitation.class))).thenReturn(invitation);

        GetInvitationResponse response = invitationService.respondToInvitation(invId, request);

        assertEquals(Status.ACCEPTED, response.getStatus());
        verify(invitationRepository).save(invitation);
    }

    @Test
    @DisplayName("Expire Old Invitations - Eski Davetleri Reddetmeli")
    void expireOldInvitations_shouldRejectPendingOnes() {
        // Arrange
        Invitation oldInv1 = new Invitation();
        oldInv1.setId(1L);
        oldInv1.setStatus(Status.PENDING);

        Invitation oldInv2 = new Invitation();
        oldInv2.setId(2L);
        oldInv2.setStatus(Status.PENDING);

        List<Invitation> oldList = Arrays.asList(oldInv1, oldInv2);

        when(invitationRepository.findAllByStatusAndCreatedAtBefore(eq(Status.PENDING), any(LocalDateTime.class)))
                .thenReturn(oldList);

        invitationService.expireOldInvitations();

        assertEquals(Status.REJECTED, oldInv1.getStatus());
        assertEquals(Status.REJECTED, oldInv2.getStatus());
        verify(invitationRepository).saveAll(oldList);
    }
}