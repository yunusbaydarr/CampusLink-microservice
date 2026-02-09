package com.CampusLink.service.concretes;

import com.CampusLink.client.UserServiceClient;
import com.CampusLink.entity.Club;
import com.CampusLink.entity.ClubMember;


import com.CampusLink.mapper.ClubMapper;
import com.CampusLink.repository.ClubMemberRepository;
import com.CampusLink.repository.ClubRepository;
import com.CampusLink.service.abstracts.ClubService;
import com.campuslink.common.cloudinary.CloudinaryService;
import com.campuslink.common.dtos.requests.ClubCreateRequest;
import com.campuslink.common.dtos.requests.ClubUpdateRequest;
import com.campuslink.common.dtos.responses.GetClubResponse;
import com.campuslink.common.dtos.responses.UserResponse;
import com.campuslink.common.enums.UserRole;
import com.campuslink.common.events.club.MemberJoinedClubEvent;
import com.campuslink.common.exceptions.ExceptionBuilder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClubServiceImpl implements ClubService {


    public static final String CLUB_EVENTS_TOPIC = "club-events-topic";



    private final ClubMapper clubMapper;
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final UserServiceClient userServiceClient;
    private final KafkaTemplate<String,Object> kafkaTemplate;
    private final CloudinaryService cloudinaryService;


    public ClubServiceImpl(ClubMapper clubMapper, ClubRepository clubRepository,
                           ClubMemberRepository clubMemberRepository,
                           UserServiceClient userServiceClient,
                           KafkaTemplate<String,Object> kafkaTemplate, CloudinaryService cloudinaryService) {
        this.clubMapper = clubMapper;
        this.clubRepository = clubRepository;
        this.clubMemberRepository = clubMemberRepository;
        this.userServiceClient=userServiceClient;
        this.kafkaTemplate=kafkaTemplate;
        this.cloudinaryService = cloudinaryService;
    }

    // Helper Method
    private Club getClubOrThrow(Long clubId){
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> ExceptionBuilder.clubNotFound(clubId));
        return club;
    }

    // Helper Method
    private ClubMember getMemberOrThrow(Long clubId, Long userId){
        ClubMember member = clubMemberRepository
                .findByClubIdAndUserId(clubId, userId)
                .orElseThrow(() -> new AccessDeniedException("Bu kulübün üyesi değilsin"));

        return member;
    }

    // Helper Method
    private ClubMember validateAdminRole(Long clubId, Long userId){

        ClubMember member = getMemberOrThrow(clubId, userId);

        if (member.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Bu kulüp için admin yetkin yok.");
        }
        return member;
    }
    // Helper Method for error decoder
    private UserResponse getRemoteUserOrThrow(Long userId) {
        return userServiceClient.getUserById(userId);
    }

    private GetClubResponse buildClubResponse(Club club) {
        List<Long> memberIds = clubMemberRepository
                .findAllByClubId(club.getId())
                .stream()
                .map(ClubMember::getUserId)
                .toList();
        // .collect(Collectors.toCollection(ArrayList::new));


        GetClubResponse response = new GetClubResponse();
        response.setId(club.getId());
        response.setName(club.getName());
        response.setDescription(club.getDescription());
        response.setCreatedByUserId(club.getCreatedByUserId());
        response.setMemberUserIds(memberIds);

        return response;
    }

//    @CacheEvict(
//            value = { "clubs" },
//            allEntries = true
//    )
    @Override
    public GetClubResponse createClub(ClubCreateRequest createRequest, MultipartFile file) {
//        UserResponse creator;
//        try{
//            creator = userServiceClient.getUserById(createRequest.getCreatedByUserId());
//        }catch (FeignException.FeignClientException.NotFound e) {
//            throw ExceptionBuilder.userNotFound(createRequest.getCreatedByUserId());
//        }catch (FeignException e){
//            throw ExceptionBuilder.user_service_error(createRequest.getCreatedByUserId());
//        }

 //       UserResponse creator = userServiceClient.getUserById(createRequest.getCreatedByUserId());

        UserResponse creator = getRemoteUserOrThrow(createRequest.getCreatedByUserId());

        Club club = new Club();
        club.setName(createRequest.getName());
        club.setDescription(createRequest.getDescription());
        club.setCreatedByUserId(creator.getId());
        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            imageUrl = cloudinaryService.uploadFile(file);
            if (imageUrl == null) {
                throw ExceptionBuilder.imageUploadFailed();
            }
        } else {
            imageUrl = "https://res.cloudinary.com/dejjmja6u/image/upload/v1769256254/default_club_photo_w6pn8z.png";
        }

        club.setClubPictureUrl(imageUrl);
        clubRepository.save(club);

        ClubMember clubMember = new ClubMember();
        clubMember.setClub(club);
        clubMember.setUserId(creator.getId());
        clubMember.setRole(UserRole.ADMIN);

        clubMemberRepository.save(clubMember);

        GetClubResponse getClubResponse = new GetClubResponse();
        getClubResponse.setName(club.getName());
        getClubResponse.setId(club.getId());
        getClubResponse.setDescription(club.getDescription());
        getClubResponse.setCreatedByUserId(creator.getId());

        List<Long> memberList = new ArrayList<>();
        memberList.add(creator.getId());
        getClubResponse.setMemberUserIds(memberList);

        return getClubResponse;
    }

//    @Cacheable(
//            value = "club",
//            key = "#id"
//    )
    @Override
    public GetClubResponse getClubById(Long id) {

        Club club = getClubOrThrow(id);

        List<Long> memberUserIds = clubMemberRepository
                .findAllByClubId(club.getId())
                .stream()
                .map(ClubMember::getUserId)
                .toList();
        //              .collect(Collectors.toCollection(ArrayList::new));

        GetClubResponse getClubResponse = new GetClubResponse();
        getClubResponse.setName(club.getName());
        getClubResponse.setId(club.getId());
        getClubResponse.setDescription(club.getDescription());
        getClubResponse.setCreatedByUserId(club.getCreatedByUserId());
        getClubResponse.setClubPictureUrl(club.getClubPictureUrl());
        getClubResponse.setMemberUserIds(memberUserIds);

        return getClubResponse;
    }

    private GetClubResponse convertToDto(Club club) {
        GetClubResponse response = new GetClubResponse();
        response.setId(club.getId());
        response.setName(club.getName());
        response.setDescription(club.getDescription());
        response.setCreatedByUserId(club.getCreatedByUserId());
        response.setClubPictureUrl(club.getClubPictureUrl());


        List<Long> memberIds = clubMemberRepository
                .findAllByClubId(club.getId())
                .stream()
                .map(ClubMember::getUserId)
                .toList();
        //.collect(Collectors.toCollection(ArrayList::new));

        response.setMemberUserIds(memberIds);
        return response;
    }

//    @Cacheable(
//            value = "clubs",
//            key = "'all'"
//    )
    @Override
    public List<GetClubResponse> getAllClubs() {

        List<Club> allClubs = clubRepository.findAll();
        return allClubs.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        //.collect(Collectors.toCollection(ArrayList::new));
    }

//    @CacheEvict(
//            value = { "clubs", "club" },
//            allEntries = true
//    )
    @Transactional
    @Override
    public GetClubResponse updateClub(ClubUpdateRequest updateRequest, Long userId) {

        validateAdminRole(updateRequest.getId(),userId);
        Club club = getClubOrThrow(updateRequest.getId());

        //club.setId(updateRequest.getId());
        club.setName(updateRequest.getName());
        club.setDescription(updateRequest.getDescription());

        Club saved = clubRepository.save(club);

//        List<Long> memberUserIds = clubMemberRepository
//                .findAllByClubId(saved.getId())
//                .stream()
//                .map(ClubMember::getUserId)
//                .toList();
//
//        GetClubResponse response = new GetClubResponse();
//        response.setId(saved.getId());
//        response.setName(saved.getName());
//        response.setDescription(saved.getDescription());
//        response.setCreatedByUserId(saved.getCreatedByUserId());
//        response.setMemberUserIds(memberUserIds);
//
//        return response;
        return buildClubResponse(saved);

    }

//    @CacheEvict(
//            value = { "clubs", "club" },
//            allEntries = true
//    )
    @Override
    @Transactional
    public void deleteClub(Long clubId, Long userId) {
        this.validateAdminRole(clubId , userId);
        this.clubRepository.deleteById(clubId);
    }

//    @CacheEvict(
//            value = { "club", "clubs" },
//            allEntries = true
//    )
    @Override
    @Transactional
    public void joinClub(Long clubId, Long userId) {
        Club club = getClubOrThrow(clubId);

        if (clubMemberRepository.existsByClubIdAndUserId(club.getId(), userId)) {
            throw ExceptionBuilder.alreadyJoinedClub(userId, clubId);
        }

        UserResponse user = getRemoteUserOrThrow(userId);

        ClubMember clubMember = new ClubMember();
        clubMember.setRole(UserRole.STUDENT);
        clubMember.setUserId(userId);
        clubMember.setClub(club);

        this.clubMemberRepository.save(clubMember);

        //Kafkayı öğrenince düzelt
//        System.out.println("🔔 Notification tetiklendi!");
//        notificationService.sendJoinClubNotification(userId, clubId);
//        System.out.println("NOTIFICATION GÖNDERİLDİ!");

//
//        MemberJoinedClubEvent event = new MemberJoinedClubEvent(
//               user.getId(),user.getName(),user.getEmail(),user.getUsername(),club.getName()
//
//        );

        MemberJoinedClubEvent event = clubMapper.toMemberJoinedClubEvent(user,club);
        kafkaTemplate.send(CLUB_EVENTS_TOPIC,event);

    }

//    @CacheEvict(
//            value = { "club", "clubs" },
//            allEntries = true
//    )
    @Override
    public void leaveClub(Long clubId, Long userId) {

        ClubMember member = getMemberOrThrow(clubId, userId);

        this.clubMemberRepository.delete(member);
    }


    @Override
    public List<Long> getClubMemberUserIds(Long clubId) {


        Club club = getClubOrThrow(clubId);
//
//        List<Long> userIds = clubMemberRepository
//                .findByClub_Id(club.getId())
//                .stream()
//                .map(ClubMember::getUserId)
//                .toList();
//
//        return userServiceClient.getUsersByIds(userIds);

        return clubMemberRepository
                .findAllByClubId(club.getId())
                .stream()
                .map(ClubMember::getUserId)
                .toList();
        //.collect(Collectors.toCollection(ArrayList::new));
    }
}
