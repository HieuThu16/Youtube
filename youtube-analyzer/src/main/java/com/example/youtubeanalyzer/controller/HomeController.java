package com.example.youtubeanalyzer.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "online");
        response.put("service", "YouTube Analyzer API");
        response.put("endpoints", new String[]{
            "/api/youtube/analyze",
            "/api/youtube/videos"
        });
        response.put("documentation", "Để phân tích kênh YouTube, hãy gửi POST request đến /api/youtube/analyze với body {\"channelUrl\": \"URL_KÊNH\"}");

        return response;
    }
}
