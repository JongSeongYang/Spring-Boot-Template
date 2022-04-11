package com.example.template.config;

import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    private final ModelMapper customModelMapper = new ModelMapper();

    @Bean
    public ModelMapper strictModelMapper() {
        customModelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return customModelMapper;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public OkHttpClient okHttpClient() { return new OkHttpClient();}

}
