package com.CampusLink.client.decoder;


import com.campuslink.common.exceptions.ExceptionBuilder;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        log.error("Feign Error Client -> Method: {}, Status: {}",
                methodKey, response.status());

            switch (response.status()){
                case 400:
                    return ExceptionBuilder.userServiceBadRequest();
                case 404:
                    return ExceptionBuilder.remoteUserNotFound();
                default:
                    if (response.status() >= 500) {
                        return ExceptionBuilder.userServiceUnavailable();
                    }
                    return errorDecoder.decode(methodKey, response);

            }
    }

}
