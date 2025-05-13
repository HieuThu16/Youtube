package com.example.youtubeanalyzer.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HomeController {

    @GetMapping("/")
    @ResponseBody
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "online");
        response.put("service", "YouTube Analyzer API");
        response.put("version", "1.0.0");
        response.put("endpoints", new String[]{
            "/api/youtube/test",
            "/api/youtube/analyze",
            "/api/youtube/videos"
        });
        response.put("usage", "Để kiểm tra API, hãy truy cập /api/youtube/test");
        response.put("documentation", "Để phân tích kênh YouTube, hãy gửi POST request đến /api/youtube/analyze với body {\"channelUrl\": \"URL_KÊNH\"}");

        return response;
    }

    @GetMapping("/api")
    public RedirectView redirectToApi() {
        return new RedirectView("/api/youtube/test");
    }
}
