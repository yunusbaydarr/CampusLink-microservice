package com.campuslink.common.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InvitationCreateRequest {

    private Long clubId;
    private Long fromUserId;
    private Long toUserId;

}
