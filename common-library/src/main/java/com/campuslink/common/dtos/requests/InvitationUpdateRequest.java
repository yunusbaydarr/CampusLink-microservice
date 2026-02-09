package com.campuslink.common.dtos.requests;


import com.campuslink.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InvitationUpdateRequest {

    private Status status;
}
