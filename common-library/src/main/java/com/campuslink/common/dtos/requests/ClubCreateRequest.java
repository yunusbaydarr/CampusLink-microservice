package com.campuslink.common.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClubCreateRequest {
    private String name;
    private String description;
    private Long createdByUserId;
    private String clubPictureUrl;
    private String title;

}
