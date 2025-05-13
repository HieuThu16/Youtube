package com.example.youtubeanalyzer.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Configuration
public class ApiConfig {

    @Value("${youtube.api.key}")
    private String youtubeApiKey;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Bean
    public YouTube youTube() throws GeneralSecurityException, IOException {
        return new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                null)
                .setApplicationName("youtube-analyzer")
                .build();
    }

    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(openaiApiKey);
    }
} 