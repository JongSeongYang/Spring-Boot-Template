package com.example.template.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Data
public class OctetConfig {

    @Value("${octet.access-token}")
    public String accessToken;

    @Value("${octet.base-url}")
    public String baseUrl;


    @Value("${octet.passphrase}")
    public String passphrase;

    @Value("${octet.private-key}")
    public String privateKey;

}
