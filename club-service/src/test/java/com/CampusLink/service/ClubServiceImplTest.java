package com.CampusLink.service;

import com.CampusLink.client.UserServiceClient;
import com.CampusLink.entity.Club;
import com.CampusLink.entity.ClubMember;
import com.CampusLink.mapper.ClubMapper;
import com.CampusLink.repository.ClubMemberRepository;
import com.CampusLink.repository.ClubRepository;
import com.CampusLink.service.concretes.ClubServiceImpl;
import com.campuslink.common.cloudinary.CloudinaryService;
import com.campuslink.common.dtos.requests.ClubCreateRequest;
import com.campuslink.common.dtos.responses.GetClubResponse;
import com.campuslink.common.dtos.responses.UserResponse;
import com.campuslink.common.enums.UserRole;
import com.campuslink.common.events.club.MemberJoinedClubEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClubServiceImplTest {

    @Mock
    private ClubMapper clubMapper;
    @Mock
    private ClubRepository clubRepository;
    @Mock
    private ClubMemberRepository clubMemberRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private ClubServiceImpl clubService;


    @Test
    @DisplayName("Create Club - Başarılı Senaryo")
    void createClub_shouldReturnResponse_whenSuccess() {
        // Arrange (Hazırlık)
        ClubCreateRequest request = new ClubCreateRequest();
        request.setName("Java Kulübü");
        request.setDescription("Java öğreniyoruz");
        request.setCreatedByUserId(1L);

        UserResponse mockUser = new UserResponse();
        mockUser.setId(1L);
        mockUser.setName("Ahmet");

        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "resim".getBytes());

        when(userServiceClient.getUserById(1L)).thenReturn(mockUser);
        when(cloudinaryService.uploadFile(any(MultipartFile.class))).thenReturn("http://resim-url.com");
        when(clubRepository.save(any(Club.class))).thenAnswer(invocation -> {
            Club c = invocation.getArgument(0);
            c.setId(100L);
            return c;
        });
        GetClubResponse response = clubService.createClub(request, file);

        assertNotNull(response);
        assertEquals("Java Kulübü", response.getName());
        assertEquals(100L, response.getId());
        assertEquals(1L, response.getCreatedByUserId());

        verify(clubRepository).save(any(Club.class));
        verify(clubMemberRepository).save(any(ClubMember.class));
    }


    @Test
    @DisplayName("Get Club By ID - Başarılı Senaryo")
    void getClubById_shouldReturnResponse_whenClubExists() {
        Long clubId = 1L;
        Club club = new Club();
        club.setId(clubId);
        club.setName("Satranç");

        when(clubRepository.findById(clubId)).thenReturn(Optional.of(club));
        when(clubMemberRepository.findAllByClubId(clubId)).thenReturn(new ArrayList<>());

        GetClubResponse response = clubService.getClubById(clubId);

        assertNotNull(response);
        assertEquals(clubId, response.getId());
        assertEquals("Satranç", response.getName());
    }

    @Test
    @DisplayName("Get Club By ID - Kulüp Bulunamazsa Hata Fırlatmalı")
    void getClubById_shouldThrowException_whenClubNotFound() {
        Long clubId = 99L;
        when(clubRepository.findById(clubId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> clubService.getClubById(clubId));
    }


    @Test
    @DisplayName("Join Club - Başarılı Katılım")
    void joinClub_shouldSaveMemberAndSendKafkaEvent() {
        // Arrange
        Long clubId = 1L;
        Long userId = 5L;

        Club club = new Club();
        club.setId(clubId);
        club.setName("Dans Kulübü");

        UserResponse user = new UserResponse();
        user.setId(userId);
        user.setUsername("user5");

        when(clubRepository.findById(clubId)).thenReturn(Optional.of(club));
        when(clubMemberRepository.existsByClubIdAndUserId(clubId, userId)).thenReturn(false);
        when(userServiceClient.getUserById(userId)).thenReturn(user);
        when(clubMapper.toMemberJoinedClubEvent(user, club)).thenReturn(new MemberJoinedClubEvent(
                userId,
                "Test User Name",
                "test@email.com",
                "user5",
                "Dans Kulübü"
        ));

        clubService.joinClub(clubId, userId);

        verify(clubMemberRepository).save(any(ClubMember.class));
        verify(kafkaTemplate).send(eq(ClubServiceImpl.CLUB_EVENTS_TOPIC), any(MemberJoinedClubEvent.class));
    }

    @Test
    @DisplayName("Join Club - Zaten Üye İse Hata Fırlatmalı")
    void joinClub_shouldThrowException_whenAlreadyMember() {
        Long clubId = 1L;
        Long userId = 5L;
        Club club = new Club();
        club.setId(clubId);

        when(clubRepository.findById(clubId)).thenReturn(Optional.of(club));
        when(clubMemberRepository.existsByClubIdAndUserId(clubId, userId)).thenReturn(true); // Zaten üye

        assertThrows(RuntimeException.class, () -> clubService.joinClub(clubId, userId));

        verify(clubMemberRepository, never()).save(any());
    }


    @Test
    @DisplayName("Delete Club - Admin Değilse AccessDenied Fırlatmalı")
    void deleteClub_shouldThrowAccessDenied_whenUserNotAdmin() {
        // Arrange
        Long clubId = 1L;
        Long userId = 2L;

        ClubMember member = new ClubMember();
        member.setUserId(userId);
        member.setRole(UserRole.STUDENT);

        when(clubMemberRepository.findByClubIdAndUserId(clubId, userId))
                .thenReturn(Optional.of(member));

        assertThrows(AccessDeniedException.class, () -> clubService.deleteClub(clubId, userId));

        verify(clubRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Delete Club - Admin İse Başarılı Silmeli")
    void deleteClub_shouldDelete_whenUserIsAdmin() {
        // Arrange
        Long clubId = 1L;
        Long userId = 2L;

        ClubMember member = new ClubMember();
        member.setUserId(userId);
        member.setRole(UserRole.ADMIN);

        when(clubMemberRepository.findByClubIdAndUserId(clubId, userId))
                .thenReturn(Optional.of(member));

        clubService.deleteClub(clubId, userId);

        verify(clubRepository).deleteById(clubId);
    }
}
