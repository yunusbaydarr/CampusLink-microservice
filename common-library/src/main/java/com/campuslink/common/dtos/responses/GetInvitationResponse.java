package com.campuslink.common.dtos.responses;


import com.campuslink.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetInvitationResponse {

    private Long id;

    private Long clubId;

    private Long fromUserId;

    private Long toUserId;

    private Status status ;

    private LocalDateTime createdAt;
}
