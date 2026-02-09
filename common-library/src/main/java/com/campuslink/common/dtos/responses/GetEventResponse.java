package com.campuslink.common.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetEventResponse {

    private Long id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime date;
    private Long createdByUserId;
    private Long clubId;
    private String eventPictureUrl;
    private List<Long> participantUserIds;


}
