package com.campuslink.common.dtos.requests;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
}
