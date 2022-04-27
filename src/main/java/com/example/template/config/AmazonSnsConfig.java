package com.example.template.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AmazonSnsConfig {

    @Value("${aws.accessKey}")
    String awsAccessKey;

    @Value("${aws.secretKey}")
    String awsSecretKey;

    @Value("${aws.ses.accessKey}")
    String awsSesAccessKey;

    @Value("${aws.ses.secretKey}")
    String awsSesSecretKey;
}
