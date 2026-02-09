package com.campuslink.common.exceptions;

public class ErrorMessageBuilder{

    private final ErrorMessage errorMessage;

    public ErrorMessageBuilder(){
        this.errorMessage = new ErrorMessage();
    }

    public ErrorMessageBuilder withMessageType(MessageType type){
        this.errorMessage.setMessageType(type);
        return this;
    }
    public ErrorMessageBuilder withStatic(String staticText) {
        this.errorMessage.setOfStatic(staticText);
        return this;
    }
    public ErrorMessage build() {
        return this.errorMessage;
    }
}
