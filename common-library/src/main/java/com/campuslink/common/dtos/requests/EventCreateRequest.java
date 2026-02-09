package com.campuslink.common.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EventCreateRequest {

    private String title;
    private String description;
    private String location;
    private LocalDateTime date;
    private String eventPictureUrl;
    private Long clubId;
    private Long createdBy;

}

