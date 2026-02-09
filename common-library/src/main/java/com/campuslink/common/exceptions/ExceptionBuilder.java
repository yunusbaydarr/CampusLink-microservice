package com.campuslink.common.exceptions;


import org.springframework.http.HttpStatus;

public class ExceptionBuilder {

    public static BaseException userNotFound(Long id){
        String message = new ErrorMessageBuilder()
                .withMessageType(MessageType.USER_NOT_FOUND)
                .withStatic(id.toString())
                .build()
                .prepareMessage();

        return new BaseException(message, HttpStatus.NOT_FOUND);
    }

    public static BaseException usernameAlreadyExists(String username){
        String message = new ErrorMessageBuilder()
                .withMessageType(MessageType.USERNAME_ALREADY_EXISTS)
                .withStatic(username)
                .build()
                .prepareMessage();

        return new BaseException(message, HttpStatus.CONFLICT);
    }
    public static BaseException emailAlreadyExists(String email){
        String message = new ErrorMessageBuilder()
                .withMessageType(MessageType.EMAIL_ALREADY_EXISTS)
                .withStatic(email)
                .build()
                .prepareMessage();

        return new BaseException(message, HttpStatus.CONFLICT);
    }
    public static BaseException imageUploadFailed(){
        String message = new ErrorMessageBuilder()
                .withMessageType(MessageType.IMAGE_UPLOAD_FAILED)
                .build()
                .prepareMessage();

        return new BaseException(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static BaseException userServiceBadRequest() {
        String message = new ErrorMessageBuilder()
                .withMessageType(MessageType.USER_SERVICE_BAD_REQUEST)
                .withStatic("Invalid request sent to the user service.")
                .build()
                .prepareMessage();
        return new BaseException(message, HttpStatus.BAD_REQUEST);
    }


    public static BaseException remoteUserNotFound() {
        String message = new ErrorMessageBuilder()
                .withMessageType(MessageType.REMOTE_USER_NOT_FOUND)
                .withStatic("Related user could not be found in the user service.")
                .build()
                .prepareMessage();
        return new BaseException(message, HttpStatus.NOT_FOUND);
    }

    public static BaseException userServiceUnavailable() {
        String message = new ErrorMessageBuilder()
                .withMessageType(MessageType.USER_SERVICE_UNAVAILABLE)
                .withStatic("User service is currently unavailable.")
                .build()
                .prepareMessage();
        return new BaseException(message, HttpStatus.SERVICE_UNAVAILABLE);
    }
    public static BaseException user_service_error(Long id){
        String message = new ErrorMessageBuilder()
                .withMessageType(MessageType.USER_SERVICE_ERROR)
                .withStatic(id.toString())
                .build()
                .prepareMessage(); // prepareMessage()

        return new BaseException(message, HttpStatus.FAILED_DEPENDENCY);
    }



    public static BaseException clubNotFound(Long id){
        String message = new ErrorMessageBuilder()
                .withMessageType(MessageType.CLUB_NOT_FOUND)
                .withStatic(id.toString())
                .build()
                .prepareMessage();

        return new BaseException(message, HttpStatus.NOT_FOUND);
    }
    public static BaseException clubMemberNotFound(Long clubId, Long userId){
        String message = new ErrorMessageBuilder()
                .withMessageType(MessageType.CLUB_MEMBER_NOT_FOUND)
                .withStatic(clubId.toString())
                .build()
                .prepareMessage();

        return new BaseException(message, HttpStatus.NOT_FOUND);
    }


    public static BaseException mailCouldNotSend(String to, Long userId){
        String message = new ErrorMessageBuilder()
                .withMessageType(MessageType.MAIL_COULD_NOT_SEND)
                .withStatic(userId.toString())
                .build()
                .prepareMessage();

        return new BaseException(message, HttpStatus.CONFLICT);
    }
    public static BaseException eventNotFound(Long id){
        String message = new ErrorMessageBuilder()
                .withMessageType(MessageType.EVENT_NOT_FOUND)
                .withStatic(id.toString())
                .build()
                .prepareMessage();

        return new BaseException(message, HttpStatus.NOT_FOUND);
    }
    public static BaseException eventParticipantNotFound(Long userId, Long eventId){
        String message = new ErrorMessageBuilder()
                .withMessageType(MessageType.EVENT_PARTICIPANT_NOT_FOUND)
                .withStatic(eventId.toString())
                .build()
                .prepareMessage();

        return new BaseException(message, HttpStatus.NOT_FOUND);
    }
    public static BaseException invitationNotFound(Long id){
        String message = new ErrorMessageBuilder()
                .withMessageType(MessageType.INVITATION_NOT_FOUND)
                .withStatic(id.toString())
                .build()
                .prepareMessage();

        return new BaseException(message, HttpStatus.NOT_FOUND);
    }
    public static BaseException alreadyPendingInvitation(Long eventId, Long userId){
        String message = new ErrorMessageBuilder()
                .withMessageType(MessageType.ALREADY_PENDING_INVITATION)
                .withStatic(eventId.toString())
                .build()
                .prepareMessage();

        return new BaseException(message, HttpStatus.NOT_FOUND);
    }
    public static BaseException invitationAlreadyResponded(Long invitationId){
        String message = new ErrorMessageBuilder()
                .withMessageType(MessageType.INVITATION_ALREADY_RESPONDED)
                .withStatic(invitationId.toString())
                .build()
                .prepareMessage();

        return new BaseException(message, HttpStatus.CONFLICT);
    }
    public static BaseException clubServiceBadRequest(){
        String message = new ErrorMessageBuilder()
                .withMessageType(MessageType.CLUB_SERVICE_BAD_REQUEST)
                .build()
                .prepareMessage();

        return new BaseException(message, HttpStatus.BAD_REQUEST);
    }

    public static BaseException remoteClubNotFound() {
        String message = new ErrorMessageBuilder()
                .withMessageType(MessageType.REMOTE_CLUB_NOT_FOUND)
                .withStatic("Related club could not be found in the user service.")
                .build()
                .prepareMessage();
        return new BaseException(message, HttpStatus.NOT_FOUND);
    }
    public static BaseException clubServiceUnavailable() {
        String message = new ErrorMessageBuilder()
                .withMessageType(MessageType.CLUB_SERVICE_UNAVAILABLE)
                .withStatic("Club service is currently unavailable.")
                .build()
                .prepareMessage();
        return new BaseException(message, HttpStatus.SERVICE_UNAVAILABLE);
    }

    public static BaseException alreadyJoinedEvent(Long eventId, Long userId) {
        String message = new ErrorMessageBuilder()
                .withMessageType(MessageType.ALREADY_JOINED_EVENT)
                .withStatic("User already joined this event")
                .build()
                .prepareMessage();
        return new BaseException(message, HttpStatus.ALREADY_REPORTED);
    }

    public static BaseException alreadyJoinedClub(Long clubId, Long userId) {
        String message = new ErrorMessageBuilder()
                .withMessageType(MessageType.ALREADY_JOINED_CLUB)
                .withStatic(" User already joined this club")
                .build()
                .prepareMessage();
        return new BaseException(message, HttpStatus.ALREADY_REPORTED);
    }




}
