package com.example.notification_service.consumer;

import com.campuslink.common.events.club.MemberJoinedClubEvent;
import com.campuslink.common.events.event.EventParticipatedEvent;
import com.campuslink.common.events.invitation.InvitationSentEvent;
import com.campuslink.common.events.user.UserCreatedEvent;
import com.campuslink.common.exceptions.ExceptionBuilder;
import com.example.notification_service.service.abstracts.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.apache.kafka.clients.consumer.ConsumerRecord;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer{

    private final MailService mailService;

    @KafkaListener(topics = "club-events-topic",groupId = "notification-group")
    public void consumerMemberJoinedEvent(MemberJoinedClubEvent event,
                                          ConsumerRecord<String,Object> consumerRecord
    ){

        log.info("NotificationConsumer.consumerMemberJoinedEvent consumed EVENT: {} " +
                        "from partition: {} " +
                        "with offset: {} " +
                        "thread: {} " +
                        "for message key: {}",
                event,
                consumerRecord.partition(),
                consumerRecord.offset(),
                Thread.currentThread().getName(),
                consumerRecord.key()
        );


        String template = """
                <h2>Merhaba {{name}},</h2>
                <p><b>{{club}}</b> kulübüne başarıyla katıldınız!</p>
                <p>Etkinliklerde görüşmek üzere 🙌</p>
                """;

        String html = template
                .replace("{{name}}", event.name())
                .replace("{{club}}", event.clubName());

        try {
            mailService.sendMail(
                    event.toEmail(),
                    "Kulübe Katılım Başarılı!",
                    html
            );
            log.info("Klübe katılım maili başarıyla gönderildi: {}", event.toEmail());

        } catch (Exception e) {
            log.error("Klübe katılım maili gönderimi başarısız! Hata: {}", e.getMessage());
            throw ExceptionBuilder.mailCouldNotSend(event.toEmail(), event.userId());
        }

    }

    @KafkaListener(topics = "invitation-events-topic", groupId = "notification-group")
    public void consumeInvitationSentEvent(InvitationSentEvent event, ConsumerRecord<String,Object> consumerRecord){

        log.info("EVENT ALINDI (Invitation) -> Offset: {} , ToUser: {}, ToUserId: {}",
                consumerRecord.offset(), event.toUserName(), event.toUserId());

        String template = """
                <h2>Merhaba {{name}},</h2>
                <p><b>{{club}}</b> kulübüne katılmanı istiyoruz. Bilgi ve becerilerini geliştirirken,
                 Klübümüze de başarılar katacağına inancımız tam.!</p>
                <p>Etkinliklerde görüşmek üzere 🙌</p>
                """;

        String html = template
                .replace("{{name}}", event.toUserName())
                .replace("{{club}}", event.clubName());


        try{
            mailService.sendMail(
                    event.toUserEmail(),
                    "Kulübe Katılım Daveti!",
                    html );

            log.info("Davet maili başarıyla gönderildi: {}", event.toUserEmail());
        }catch (Exception e ){
            log.error("Davet maili gönderimi başarısız! Hata: {}", e.getMessage());
            throw ExceptionBuilder.mailCouldNotSend(event.toUserName(), event.toUserId());
        }


    }

    @KafkaListener(topics = "event-events-topic" , groupId = "notification-group")
    public void consumeEventParticipatedEvent(EventParticipatedEvent event,
                                              ConsumerRecord<String, Object> consumerRecord){

        log.info("EVENT ALINDI (Event) -> Offset: {}, User: {}", consumerRecord.offset(), event.userName());

        String template = """
                <h2>Merhaba <b>{{name}}</b>,</h2>
                <p><b>{{event}}</b> etkinliğine başarıyla katıldınız!</p>
                <p>Bol eğlence ve bilgi diliyoruz. 🙌</p>
                """;


        String html = template
                .replace("{{name}}", event.userName().toUpperCase())
                .replace("{{event}}", event.eventTitle());

        try {
            mailService.sendMail(event.userEmail(), "Etkinliğe Kayıt Başarılı!", html);
            log.info("Etkinlik maili gönderildi: {}", event.userEmail());
        } catch (Exception e) {
            log.error("Etkinlik maili hatası: {}", e.getMessage());
            throw ExceptionBuilder.mailCouldNotSend(event.userEmail(), event.userId());
        }

    }

    @KafkaListener(topics = "user-events-topic", groupId = "notification-group")
    public void consumeUserCreatedEvent(UserCreatedEvent event,
                                        ConsumerRecord<String, Object> consumerRecord) {

        log.info("EVENT ALINDI (User Created) -> Offset: {}, Email: {}", consumerRecord.offset(), event.email());

        String html = """
                <h2>Aramıza Hoşgeldin %s! 🎉</h2>
                <p>CampusLink ailesine katıldığın için çok mutluyuz.</p>
                <p>Hemen ilgini çeken kulüpleri keşfetmeye başla.</p>
                <p>İyi eğlenceler!</p>
                """.formatted(event.name());

        try {
            mailService.sendMail(event.email(), "CampusLink'e Hoşgeldin!", html);
            log.info("Hoşgeldin maili gönderildi: {}", event.email());
        } catch (Exception e) {
            log.error("Hoşgeldin maili hatası: {}", e.getMessage());
            throw ExceptionBuilder.mailCouldNotSend(event.email(), event.userId());
        }
    }


}
