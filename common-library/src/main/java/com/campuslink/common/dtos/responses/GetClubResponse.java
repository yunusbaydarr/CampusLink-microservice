package com.campuslink.common.dtos.responses;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetClubResponse {
    private Long id;
    private String name;
    private String description;
    private Long createdByUserId;
    private String clubPictureUrl;
    private List<Long> memberUserIds;

}
