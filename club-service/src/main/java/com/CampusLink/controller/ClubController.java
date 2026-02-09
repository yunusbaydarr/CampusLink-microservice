package com.CampusLink.controller;


import com.CampusLink.service.abstracts.ClubService;
import com.campuslink.common.dtos.requests.ClubCreateRequest;
import com.campuslink.common.dtos.requests.ClubUpdateRequest;
import com.campuslink.common.dtos.responses.GetClubResponse;
import com.campuslink.common.security.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/club")

public class ClubController {

    private final ClubService clubService;

    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }


    @PostMapping(value = "/createClub", consumes = "multipart/form-data")
    public ResponseEntity<GetClubResponse> createClub(@RequestPart("club") ClubCreateRequest createRequest,
                                                      @RequestPart(value = "file", required = false) MultipartFile file,
                                                      @AuthenticationPrincipal AuthenticatedUser user){

        createRequest.setCreatedByUserId(user.getId());

        GetClubResponse club = clubService.createClub(createRequest,file);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(club.getId())
                .toUri();

        return ResponseEntity.created(location).body(club);

    }

    @GetMapping("/getClubById/{id}")
    public ResponseEntity<GetClubResponse> getClubById(@PathVariable Long id){
        GetClubResponse club = clubService.getClubById(id);
        return ResponseEntity.ok(club);
    }

    @GetMapping("/getAllClubs")
    public ResponseEntity<List<GetClubResponse>> getAllClubs(){

        List<GetClubResponse> allClubs = clubService.getAllClubs();

        return ResponseEntity.ok(allClubs);

    }

    @PutMapping("/updateClub")
    public ResponseEntity<GetClubResponse> updateClub(@RequestBody ClubUpdateRequest updateRequest , @AuthenticationPrincipal AuthenticatedUser user){

        GetClubResponse getClubResponse = clubService.updateClub(updateRequest , user.getId());
        return ResponseEntity.ok(getClubResponse);
    }

    @DeleteMapping("/deleteClub/{clubId}")
    public ResponseEntity<Void> deleteClub(@PathVariable Long clubId , @AuthenticationPrincipal AuthenticatedUser user){

        this.clubService.deleteClub(clubId , user.getId());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/joinClub/{clubId}")
    public ResponseEntity<Void> joinClub(@PathVariable Long clubId, @AuthenticationPrincipal AuthenticatedUser user){

        this.clubService.joinClub(clubId, user.getId());
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/leaveClub/{clubId}")
    public ResponseEntity<Void> leaveClub(@PathVariable Long clubId, @AuthenticationPrincipal AuthenticatedUser user){
        this.clubService.leaveClub(clubId,user.getId());

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/getClubMembers/{clubId}")
    public ResponseEntity<List<Long>> getClubMembers(@PathVariable Long clubId) {

        List<Long> members = clubService.getClubMemberUserIds(clubId);
        return ResponseEntity.ok(members);
    }


}

