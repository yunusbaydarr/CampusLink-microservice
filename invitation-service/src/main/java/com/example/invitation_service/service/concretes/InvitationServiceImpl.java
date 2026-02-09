package com.example.invitation_service.service.concretes;


import com.campuslink.common.dtos.requests.InvitationCreateRequest;
import com.campuslink.common.dtos.requests.InvitationUpdateRequest;
import com.campuslink.common.dtos.responses.GetClubResponse;
import com.campuslink.common.dtos.responses.GetInvitationResponse;
import com.campuslink.common.enums.Status;
import com.campuslink.common.events.invitation.InvitationSentEvent;
import com.campuslink.common.exceptions.ExceptionBuilder;
import com.example.invitation_service.client.ClubServiceClient;
import com.example.invitation_service.client.UserServiceClient;
import com.campuslink.common.dtos.responses.UserResponse;
import com.example.invitation_service.entity.Invitation;
import com.example.invitation_service.mapper.InvitationMapper;
import com.example.invitation_service.repository.InvitationRepository;
import com.example.invitation_service.service.abstracts.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {

   public static final String INVITATION_EVENTS_TOPIC = "invitation-events-topic";

   private final InvitationMapper invitationMapper;
   private final InvitationRepository invitationRepository;
   private final UserServiceClient userServiceClient;
   private final ClubServiceClient clubServiceClient;
   private final KafkaTemplate<String,Object> kafkaTemplate;


    private GetInvitationResponse mapToResponse(Invitation invitation){
       GetInvitationResponse getInvitationResponse = new GetInvitationResponse();
       getInvitationResponse.setId(invitation.getId());
       getInvitationResponse.setStatus(invitation.getStatus());
       getInvitationResponse.setCreatedAt(invitation.getCreatedAt());
       getInvitationResponse.setClubId(invitation.getClubId());
       getInvitationResponse.setToUserId(invitation.getToUserId());
       getInvitationResponse.setFromUserId(invitation.getFromUserId());
       return getInvitationResponse;
   }
    @Override
    @Transactional
//    @CacheEvict(
//            value = "incoming_invitations",
//            key = "#request.toUserId"
//    )
    public GetInvitationResponse sendInvitation(InvitationCreateRequest request) {

        GetClubResponse clubById = clubServiceClient.getClubById(request.getClubId());
        UserResponse fromUser = userServiceClient.getUserById(request.getFromUserId());
        UserResponse toUser = userServiceClient.getUserById(request.getToUserId());

        if (invitationRepository.existsByClubAndToUserWithStatus(request.getClubId(), request.getToUserId(), Status.PENDING)){
            throw ExceptionBuilder.alreadyPendingInvitation(request.getClubId(),request.getToUserId());
        }

        Invitation invitation = new Invitation();
        invitation.setFromUserId(request.getFromUserId());
        invitation.setToUserId(request.getToUserId());
        invitation.setClubId(request.getClubId());
        invitation.setStatus(Status.PENDING);
        invitation.setCreatedAt(LocalDateTime.now());

        Invitation saved = invitationRepository.save(invitation);



      //  notificationService.sendInvitationMail(request.getFromUserId(), request.getToUserId(), request.getClubId());

        InvitationSentEvent event = invitationMapper.toInvitationSentEvent(toUser,fromUser,clubById);

        kafkaTemplate.send(INVITATION_EVENTS_TOPIC,event);
        return mapToResponse(saved);


    }

        @Override
        @Transactional
//        @CacheEvict(
//                value = "incoming_invitations",
//                allEntries = true
//        )
        public GetInvitationResponse respondToInvitation(Long invitationId, InvitationUpdateRequest request) {
           Invitation invitation = invitationRepository.findById(invitationId)
                   .orElseThrow(()-> ExceptionBuilder.invitationNotFound(invitationId));

           if (invitation.getStatus()!= Status.PENDING){
               throw new IllegalStateException("Invitation already responded");
           }
           invitation.setStatus(request.getStatus());
           Invitation updated = invitationRepository.save(invitation);

            return mapToResponse(updated);
        }

    @Override
    @Transactional(readOnly = true)
//    @Cacheable(
//            value = "incoming_invitations",
//            key = "#userId"
//    )
    public List<GetInvitationResponse> getIncomingInvitations(Long userId) {

       List<Invitation> invitationList = invitationRepository.findAllByToUserIdOrderByCreatedAtDesc(userId);
       return invitationList.stream()
               .map(this::mapToResponse)
               .toList();
    }

    @Override
    @Transactional
//    @CacheEvict(
//            value = "incoming_invitations",
//            allEntries = true
//    )
    public void expireOldInvitations() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        List<Invitation> oldPendingInvitations
                = invitationRepository.findAllByStatusAndCreatedAtBefore(Status.PENDING, oneWeekAgo);

        for (Invitation invitation : oldPendingInvitations){
            invitation.setStatus(Status.REJECTED);
        }

       invitationRepository.saveAll(oldPendingInvitations);
   }


}
