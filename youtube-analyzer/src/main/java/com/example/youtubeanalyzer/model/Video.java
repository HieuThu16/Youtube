package com.example.youtubeanalyzer.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String videoId;
    private String title;
    private String channelId;
    private String channelTitle;

    private Long viewCount;
    private Long likeCount;
    private LocalDateTime publishedAt;

    @Column(length = 5000)
    private String summary;

    private LocalDateTime analyzedAt;

    public Video() {
    }

    public Video(Long id, String videoId, String title, String channelId, String channelTitle,
            Long viewCount, Long likeCount, LocalDateTime publishedAt, String summary,
            LocalDateTime analyzedAt) {
        this.id = id;
        this.videoId = videoId;
        this.title = title;
        this.channelId = channelId;
        this.channelTitle = channelTitle;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.publishedAt = publishedAt;
        this.summary = summary;
        this.analyzedAt = analyzedAt;
    }

    public static VideoBuilder builder() {
        return new VideoBuilder();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Video video = (Video) o;
        return Objects.equals(id, video.id)
                && Objects.equals(videoId, video.videoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, videoId);
    }

    // Builder class
    public static class VideoBuilder {

        private Long id;
        private String videoId;
        private String title;
        private String channelId;
        private String channelTitle;
        private Long viewCount;
        private Long likeCount;
        private LocalDateTime publishedAt;
        private String summary;
        private LocalDateTime analyzedAt;

        VideoBuilder() {
        }

        public VideoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public VideoBuilder videoId(String videoId) {
            this.videoId = videoId;
            return this;
        }

        public VideoBuilder title(String title) {
            this.title = title;
            return this;
        }

        public VideoBuilder channelId(String channelId) {
            this.channelId = channelId;
            return this;
        }

        public VideoBuilder channelTitle(String channelTitle) {
            this.channelTitle = channelTitle;
            return this;
        }

        public VideoBuilder viewCount(Long viewCount) {
            this.viewCount = viewCount;
            return this;
        }

        public VideoBuilder likeCount(Long likeCount) {
            this.likeCount = likeCount;
            return this;
        }

        public VideoBuilder publishedAt(LocalDateTime publishedAt) {
            this.publishedAt = publishedAt;
            return this;
        }

        public VideoBuilder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public VideoBuilder analyzedAt(LocalDateTime analyzedAt) {
            this.analyzedAt = analyzedAt;
            return this;
        }

        public Video build() {
            return new Video(id, videoId, title, channelId, channelTitle, viewCount, likeCount, publishedAt, summary, analyzedAt);
        }
    }
}
