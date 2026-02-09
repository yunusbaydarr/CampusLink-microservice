package com.campuslink.common.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetEventParticipateResponse {

    private Long id;
    private String eventTitle;
    private Long userId;
    private Date joinedAt;
}
