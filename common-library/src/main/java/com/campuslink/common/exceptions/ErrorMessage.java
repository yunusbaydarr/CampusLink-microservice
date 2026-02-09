package com.campuslink.common.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorMessage {

    private MessageType messageType;
    private String ofStatic;

    public String prepareMessage(){
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(messageType.getMessage());

        if (this.ofStatic!=null){
            stringBuilder.append("Aranan id").append(": ").append(ofStatic);
        }

        return stringBuilder.toString();
    }
}
