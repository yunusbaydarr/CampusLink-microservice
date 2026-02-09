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
public class EventUpdateRequest {

    private Long id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime date;
}
