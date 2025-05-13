package com.example.youtubeanalyzer.service;

import com.example.youtubeanalyzer.model.Video;
import com.example.youtubeanalyzer.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class VideoAnalyzerService {

    @Autowired
    private YouTubeService youTubeService;

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private VideoRepository videoRepository;

    public List<Video> analyzeChannelVideos(String channelUrl) throws IOException {
        // Get channel ID from URL
        String channelId = youTubeService.getChannelIdFromUrl(channelUrl);
        if (channelId == null) {
            throw new IllegalArgumentException("Invalid YouTube channel URL or channel not found");
        }

        // Get latest videos
        List<com.google.api.services.youtube.model.Video> youtubeVideos = youTubeService.getLatestVideosFromChannel(channelId);
        
        List<Video> analyzedVideos = new ArrayList<>();

        for (com.google.api.services.youtube.model.Video ytVideo : youtubeVideos) {
            String videoId = ytVideo.getId();
            
            // Check if already analyzed
            Optional<Video> existingVideo = videoRepository.findByVideoId(videoId);
            if (existingVideo.isPresent()) {
                analyzedVideos.add(existingVideo.get());
                continue;
            }
            
            // Extract video information
            String title = ytVideo.getSnippet().getTitle();
            String description = ytVideo.getSnippet().getDescription();
            Long viewCount = ytVideo.getStatistics().getViewCount().longValue();
            Long likeCount = ytVideo.getStatistics().getLikeCount().longValue();
            Date publishedDate = new Date(ytVideo.getSnippet().getPublishedAt().getValue());
            LocalDateTime publishedAt = LocalDateTime.ofInstant(
                    publishedDate.toInstant(), ZoneId.systemDefault());
            
            // Generate summary with GPT
            String summary = openAIService.generateVideoSummary(title, description);
            
            // Create and save video entity
            Video video = Video.builder()
                    .videoId(videoId)
                    .title(title)
                    .channelId(channelId)
                    .channelTitle(ytVideo.getSnippet().getChannelTitle())
                    .viewCount(viewCount)
                    .likeCount(likeCount)
                    .publishedAt(publishedAt)
                    .summary(summary)
                    .analyzedAt(LocalDateTime.now())
                    .build();
            
            Video savedVideo = videoRepository.save(video);
            analyzedVideos.add(savedVideo);
        }
        
        return analyzedVideos;
    }

    public List<Video> getAnalyzedVideosByChannel(String channelUrl) throws IOException {
        String channelId = youTubeService.getChannelIdFromUrl(channelUrl);
        if (channelId == null) {
            throw new IllegalArgumentException("Invalid YouTube channel URL or channel not found");
        }
        
        return videoRepository.findByChannelId(channelId);
    }
} 