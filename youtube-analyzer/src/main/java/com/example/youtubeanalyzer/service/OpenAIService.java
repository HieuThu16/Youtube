package com.example.youtubeanalyzer.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

@Service
public class OpenAIService {

    @Autowired
    private OpenAiService openAiService;

    public String generateVideoSummary(String videoTitle, String videoDescription) {
        List<ChatMessage> messages = new ArrayList<>();

        // System message to set context
        messages.add(new ChatMessage("system",
                "You are an AI assistant that summarizes YouTube videos based on their title and description. "
                + "Provide a concise summary in about 100 words."));

        // User message with video information
        messages.add(new ChatMessage("user",
                "Please summarize this YouTube video:\n"
                + "Title: " + videoTitle + "\n"
                + "Description: " + videoDescription));

        // Create completion request
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .maxTokens(250)
                .temperature(0.7)
                .build();

        // Get response
        String summary = openAiService.createChatCompletion(completionRequest)
                .getChoices().get(0).getMessage().getContent();

        return summary;
    }
}
