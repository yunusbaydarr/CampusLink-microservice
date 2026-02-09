package com.example.invitation_service.client.decoder;


import com.campuslink.common.exceptions.ExceptionBuilder;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        log.error(
                "Feign Error -> MethodKey: {}, Status: {}",
                methodKey, response.status()
        );

        if (methodKey.contains("UserClient")) {
            return handleUserErrors(response.status());
        }

        if (methodKey.contains("ClubClient")) {
            return handleClubErrors(response.status());
        }

        return defaultDecoder.decode(methodKey, response);
    }

    private Exception handleUserErrors(int status) {
        return switch (status) {
            case 400 -> ExceptionBuilder.userServiceBadRequest();
            case 404 -> ExceptionBuilder.remoteUserNotFound();
            default -> ExceptionBuilder.userServiceUnavailable();
        };
    }

    private Exception handleClubErrors(int status) {
        return switch (status) {
            case 400 -> ExceptionBuilder.clubServiceBadRequest();
            case 404 -> ExceptionBuilder.remoteClubNotFound();
            default -> ExceptionBuilder.clubServiceUnavailable();
        };
    }

}
