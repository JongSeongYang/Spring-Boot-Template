package com.example.template.utils;

import com.example.template.dto.Notifi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class Fcm {

    private static final Logger logger = LoggerFactory.getLogger(Fcm.class);

    @Value("${fcm.key.path")
    private String path;

    @PostConstruct
    public void fcm() throws IOException {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(path).getInputStream()))
                .build();
        FirebaseApp app = FirebaseApp.initializeApp(options);
    }

    public Message createAllPlatformsMessage(Notifi.NotificationRequest request) {
        Message message = Message.builder()
                .setNotification(new Notification(request.getTitle(), request.getBody()))
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(3600 * 1000)
                        .setCollapseKey(request.getTopic())
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setNotification(AndroidNotification.builder()
//                                .setColor("#ffffff")
                                .setSound("doorbell.mp3")
                                .setTag(request.getTopic())
                                .build())
                        .build())
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder()
                                .setBadge(42)
                                .setCategory(request.getTopic())
                                .setThreadId(request.getTopic())
                                .setSound("doorbell.aiff")
                                .build())
                        .build())
                .setTopic(request.getTopic())
                .build();
        return message;
    }

    public String sendMessage(Notifi.NotificationRequest request) throws ExecutionException, InterruptedException {
        Message message = createAllPlatformsMessage(request);
        return FirebaseMessaging.getInstance().sendAsync(message).get();
    }

    public void subscribeToTopic(Notifi.SubscribeRequest request) throws FirebaseMessagingException {
        ApiFuture<TopicManagementResponse> response = FirebaseMessaging.getInstance().subscribeToTopicAsync(request.getTokens(), request.getTopic());
    }

    public void unsubscribeFromTopic(Notifi.SubscribeRequest request) throws FirebaseMessagingException {
        ApiFuture<TopicManagementResponse> response = FirebaseMessaging.getInstance().unsubscribeFromTopicAsync(
                request.getTokens(), request.getTopic());
    }

    public String sendPerDevices(List<String> registrationTokens, Notifi.NotificationRequest appPush) throws ExecutionException, InterruptedException {
        MulticastMessage message = MulticastMessage.builder()
                .putData("subject", appPush.getTitle())
                .putData("content", appPush.getBody())
                .putData("topic", appPush.getTopic())
                .addAllTokens(registrationTokens)
                .build();
        BatchResponse response = FirebaseMessaging.getInstance().sendMulticastAsync(message).get();

        if (response.getFailureCount() > 0) {
           logger.info("Fcm Failure num : "+ response.getFailureCount());
        }
        return "" + response.getFailureCount();
    }

    public String sendOneDevice(String registrationToken, Notifi.NotificationRequest appPush) throws ExecutionException, InterruptedException {
        Message message = makeMessage(appPush, registrationToken);
        try {
            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
            logger.info("Send fcm push to " + registrationToken + "\nResponse : " + response);
            return response;
        }catch (Exception e){
            logger.error("Fcm push Send Fail");
            return "Fcm push Send Fail";
        }

    }

    // 개별 fcm 보낼 때 사용하는 message 형식
    public Message makeMessage(Notifi.NotificationRequest request, String token) {
        Message message = Message.builder()
                // .setTopic(appPushEntity.getTopic())
                .setNotification(new Notification(request.getTitle(), request.getBody()))
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(3600 * 1000)
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .putData("topic", request.getTopic())
                        .setNotification(AndroidNotification.builder()
                                .setClickAction("FLUTTER_NOTIFICATION_CLICK")
                                .build())
                        .build())
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder()
                                .setBadge(42)
                                .putCustomData("topic", request.getTopic())
                                // .setSound("doorbell.aiff")
                                .setCategory("FLUTTER_NOTIFICATION_CLICK")
                                .build())
                        .build())
                .setToken(token)
                .build();
        return message;
    }
}
