package com.campuslink.common.handler;




import com.campuslink.common.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;


@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(value = {BaseException.class})
    public ResponseEntity<ApiError> handleBaseException
                                            (BaseException exception, WebRequest request) throws UnknownHostException {


        return ResponseEntity.status(exception.getStatus())
                .body(createApiError(exception.getMessage(), exception.getStatus(), request));

    }



    public <E> ApiError<E> createApiError(E message,HttpStatus status, WebRequest request) throws UnknownHostException {

        ApiError<E> error = new ApiError<>();
        error.setStatus(status.value());

        Exception<E> ex = new Exception<>();
        ex.setCreateTime(new Date());
        ex.setHostname(InetAddress.getLocalHost().getHostName());
        ex.setIp(InetAddress.getLocalHost().getHostAddress());
        ex.setPath(request.getDescription(false).substring(4));
        ex.setMessage(message);

        error.setException(ex);
        return error;
    }

}
