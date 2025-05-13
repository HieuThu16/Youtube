package com.example.youtubeanalyzer.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.VideoListResponse;

@Service
public class YouTubeService {

    private static final Logger logger = Logger.getLogger(YouTubeService.class.getName());

    @Autowired
    private YouTube youTube;

    @Value("${youtube.api.key}")
    private String apiKey;

    @Value("${app.videos.per.batch}")
    private int videosPerBatch;

    public String getChannelIdFromUrl(String channelUrl) throws IOException {
        logger.info("Đang lấy channel ID từ URL: " + channelUrl);

        // Xử lý trường hợp URL chứa @username
        if (channelUrl.contains("/@")) {
            String username = channelUrl.substring(channelUrl.lastIndexOf("/@") + 2);
            // Loại bỏ các tham số query nếu có
            if (username.contains("?")) {
                username = username.substring(0, username.indexOf("?"));
            }
            logger.info("Phát hiện URL loại @username: " + username);

            // Gọi API để tìm channel ID từ username
            YouTube.Channels.List request = youTube.channels()
                    .list("id")
                    .setForUsername(username)
                    .setKey(apiKey);

            ChannelListResponse response = request.execute();

            if (response.getItems() != null && !response.getItems().isEmpty()) {
                String channelId = response.getItems().get(0).getId();
                logger.info("Đã tìm thấy channel ID: " + channelId);
                return channelId;
            }

            // Thử tìm bằng custom URL
            request = youTube.channels()
                    .list("id")
                    .setForUsername(username)
                    .setKey(apiKey);

            response = request.execute();

            if (response.getItems() != null && !response.getItems().isEmpty()) {
                String channelId = response.getItems().get(0).getId();
                logger.info("Đã tìm thấy channel ID từ custom URL: " + channelId);
                return channelId;
            }

            logger.warning("Không tìm thấy channel ID cho username: " + username);
        } // Xử lý URL dạng channel/ID
        else if (channelUrl.contains("/channel/")) {
            String channelId = channelUrl.substring(channelUrl.lastIndexOf("/channel/") + 9);
            // Loại bỏ các tham số query nếu có
            if (channelId.contains("?")) {
                channelId = channelId.substring(0, channelId.indexOf("?"));
            }
            logger.info("Đã tìm thấy channel ID từ URL: " + channelId);
            return channelId;
        }

        logger.warning("Không tìm thấy channel ID từ URL: " + channelUrl);
        return null;
    }

    public List<com.google.api.services.youtube.model.Video> getLatestVideosFromChannel(String channelId) throws IOException {
        logger.info("Lấy video mới nhất từ kênh: " + channelId);

        // Search for videos from this channel
        YouTube.Search.List searchRequest = youTube.search()
                .list("id")
                .setKey(apiKey)
                .setChannelId(channelId)
                .setOrder("date")
                .setType("video")
                .setMaxResults((long) videosPerBatch);

        SearchListResponse searchResponse = searchRequest.execute();
        logger.info("Số lượng kết quả tìm thấy: " + (searchResponse.getItems() != null ? searchResponse.getItems().size() : 0));

        List<String> videoIds = new ArrayList<>();
        searchResponse.getItems().forEach(item -> videoIds.add(item.getId().getVideoId()));
        logger.info("Video IDs: " + String.join(", ", videoIds));

        // Get video details
        YouTube.Videos.List videosRequest = youTube.videos()
                .list("snippet,statistics")
                .setKey(apiKey)
                .setId(String.join(",", videoIds));

        VideoListResponse videosResponse = videosRequest.execute();
        logger.info("Số lượng video chi tiết đã lấy: " + (videosResponse.getItems() != null ? videosResponse.getItems().size() : 0));

        return videosResponse.getItems();
    }
}
