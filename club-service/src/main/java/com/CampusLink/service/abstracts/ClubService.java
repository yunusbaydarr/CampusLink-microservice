package com.CampusLink.service.abstracts;



import com.campuslink.common.dtos.requests.ClubCreateRequest;
import com.campuslink.common.dtos.requests.ClubUpdateRequest;
import com.campuslink.common.dtos.responses.GetClubResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ClubService {

    GetClubResponse createClub(ClubCreateRequest createRequest, MultipartFile file);

    GetClubResponse getClubById(Long id);

    List<GetClubResponse> getAllClubs();

    GetClubResponse updateClub(ClubUpdateRequest updateRequest , Long userId);

    void deleteClub(Long clubId , Long userId);

    void joinClub(Long clubId, Long userId);

    void leaveClub(Long clubId, Long userId);

    public List<Long> getClubMemberUserIds(Long clubId);

}
