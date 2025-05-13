package com.example.youtubeanalyzer.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.youtubeanalyzer.model.Video;
import com.example.youtubeanalyzer.service.VideoAnalyzerService;

@RestController
@RequestMapping("/api/youtube")
public class VideoAnalyzerController {

    private static final Logger logger = Logger.getLogger(VideoAnalyzerController.class.getName());

    @Autowired
    private VideoAnalyzerService videoAnalyzerService;

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        logger.info("Test endpoint được gọi");
        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "message", "API đang hoạt động",
                "timestamp", System.currentTimeMillis()
        ));
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeChannel(@RequestBody Map<String, String> request) {
        String channelUrl = request.get("channelUrl");
        logger.info("Nhận request phân tích kênh: " + channelUrl);

        try {
            if (channelUrl == null || channelUrl.isEmpty()) {
                logger.warning("URL kênh bị trống");
                return ResponseEntity.badRequest().body(Map.of("error", "Channel URL is required"));
            }

            // Chuẩn hóa URL nếu cần
            if (!channelUrl.startsWith("http")) {
                channelUrl = "https://www.youtube.com/" + channelUrl.replace("youtube.com/", "");
                logger.info("URL kênh sau khi chuẩn hóa: " + channelUrl);
            }

            logger.info("Bắt đầu phân tích kênh: " + channelUrl);
            List<Video> videos = videoAnalyzerService.analyzeChannelVideos(channelUrl);
            logger.info("Đã phân tích hoàn tất, tìm thấy " + videos.size() + " video");

            return ResponseEntity.ok(videos);
        } catch (IllegalArgumentException e) {
            logger.warning("Lỗi đối số khi phân tích kênh '" + channelUrl + "': " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Lỗi I/O khi truy cập YouTube API cho kênh '" + channelUrl + "': " + e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "Error accessing YouTube API: " + e.getMessage()));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi không xác định khi phân tích kênh '" + channelUrl + "'", e);
            return ResponseEntity.status(500).body(Map.of("error", "Unexpected error: " + e.getMessage()));
        }
    }

    @GetMapping("/videos")
    public ResponseEntity<?> getChannelVideos(@RequestParam String channelUrl) {
        logger.info("Nhận request lấy video từ kênh: " + channelUrl);

        try {
            // Chuẩn hóa URL nếu cần
            if (!channelUrl.startsWith("http")) {
                channelUrl = "https://www.youtube.com/" + channelUrl.replace("youtube.com/", "");
                logger.info("URL kênh sau khi chuẩn hóa: " + channelUrl);
            }

            List<Video> videos = videoAnalyzerService.getAnalyzedVideosByChannel(channelUrl);
            logger.info("Đã lấy " + videos.size() + " video từ kênh");

            return ResponseEntity.ok(videos);
        } catch (IllegalArgumentException e) {
            logger.warning("Lỗi đối số khi lấy video kênh '" + channelUrl + "': " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Lỗi I/O khi truy cập YouTube API cho kênh '" + channelUrl + "'", e);
            return ResponseEntity.status(500).body(Map.of("error", "Error accessing YouTube API: " + e.getMessage()));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi không xác định khi lấy video kênh '" + channelUrl + "'", e);
            return ResponseEntity.status(500).body(Map.of("error", "Unexpected error: " + e.getMessage()));
        }
    }
}
