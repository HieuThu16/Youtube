package com.example.youtubeanalyzer.repository;

import com.example.youtubeanalyzer.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    Optional<Video> findByVideoId(String videoId);
    List<Video> findByChannelId(String channelId);
} 