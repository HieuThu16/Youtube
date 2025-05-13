package com.example.youtubeanalyzer.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
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

        try {
            // Xử lý trường hợp URL chứa @username
            if (channelUrl.contains("/@")) {
                String username = channelUrl.substring(channelUrl.lastIndexOf("/@") + 2);
                // Loại bỏ các tham số query nếu có
                if (username.contains("?")) {
                    username = username.substring(0, username.indexOf("?"));
                }
                // Loại bỏ dấu / nếu có ở cuối
                if (username.endsWith("/")) {
                    username = username.substring(0, username.length() - 1);
                }
                logger.info("Phát hiện URL loại @username: " + username);

                // Phương pháp 1: Tìm thông qua search API với tên kênh chính xác
                try {
                    logger.info("Tìm kiếm kênh với query: " + username);
                    YouTube.Search.List searchRequest = youTube.search()
                            .list("snippet")
                            .setQ(username)
                            .setType("channel")
                            .setMaxResults(5L)
                            .setKey(apiKey);

                    SearchListResponse searchResponse = searchRequest.execute();
                    if (searchResponse.getItems() != null && !searchResponse.getItems().isEmpty()) {
                        // Tìm kênh có tên gần với username
                        for (int i = 0; i < searchResponse.getItems().size(); i++) {
                            String channelTitle = searchResponse.getItems().get(i).getSnippet().getChannelTitle();
                            logger.info("Kênh #" + (i + 1) + ": " + channelTitle);

                            if (channelTitle.toLowerCase().contains(username.toLowerCase())
                                    || username.toLowerCase().contains(channelTitle.toLowerCase())) {
                                String channelId = searchResponse.getItems().get(i).getId().getChannelId();
                                logger.info("Đã tìm thấy channel ID qua tìm kiếm (tên phù hợp): " + channelId);
                                return channelId;
                            }
                        }

                        // Nếu không tìm thấy tên phù hợp, lấy kênh đầu tiên
                        String channelId = searchResponse.getItems().get(0).getId().getChannelId();
                        logger.info("Đã tìm thấy channel ID qua tìm kiếm (kết quả đầu tiên): " + channelId);
                        return channelId;
                    } else {
                        logger.info("Không tìm thấy kết quả khi tìm kiếm với query: " + username);
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Lỗi khi tìm kiếm kênh qua search API: " + e.getMessage(), e);
                }

                // Phương pháp 2: Tìm thông qua search API với @username
                try {
                    logger.info("Tìm kiếm kênh với query: @" + username);
                    YouTube.Search.List searchRequest = youTube.search()
                            .list("snippet")
                            .setQ("@" + username)
                            .setType("channel")
                            .setMaxResults(3L)
                            .setKey(apiKey);

                    SearchListResponse searchResponse = searchRequest.execute();
                    if (searchResponse.getItems() != null && !searchResponse.getItems().isEmpty()) {
                        String channelId = searchResponse.getItems().get(0).getId().getChannelId();
                        logger.info("Đã tìm thấy channel ID qua tìm kiếm (@): " + channelId);
                        return channelId;
                    } else {
                        logger.info("Không tìm thấy kết quả khi tìm kiếm với query: @" + username);
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Lỗi khi tìm kiếm kênh qua search API với @: " + e.getMessage(), e);
                }

                // Phương pháp 3: Tìm video của kênh
                try {
                    // Thử tìm video của kênh sau đó lấy channelId từ video đó
                    logger.info("Tìm kiếm video với query: " + username);
                    YouTube.Search.List searchRequest = youTube.search()
                            .list("snippet")
                            .setQ(username)
                            .setType("video")
                            .setMaxResults(5L)
                            .setKey(apiKey);

                    SearchListResponse searchResponse = searchRequest.execute();
                    if (searchResponse.getItems() != null && !searchResponse.getItems().isEmpty()) {
                        logger.info("Tìm thấy " + searchResponse.getItems().size() + " video");
                        // Duyệt qua các video để tìm video của kênh có tên như username
                        for (int i = 0; i < searchResponse.getItems().size(); i++) {
                            String channelTitle = searchResponse.getItems().get(i).getSnippet().getChannelTitle();
                            logger.info("Video #" + (i + 1) + " - Channel title: " + channelTitle);

                            if (channelTitle.toLowerCase().contains(username.toLowerCase())
                                    || username.toLowerCase().contains(channelTitle.toLowerCase())) {
                                String channelId = searchResponse.getItems().get(i).getSnippet().getChannelId();
                                logger.info("Đã tìm thấy channel ID qua video: " + channelId);
                                return channelId;
                            }
                        }

                        // Nếu không tìm thấy theo tên, lấy luôn channel ID từ video đầu tiên
                        String channelId = searchResponse.getItems().get(0).getSnippet().getChannelId();
                        logger.info("Lấy channel ID từ video đầu tiên (không khớp tên): " + channelId);
                        return channelId;
                    } else {
                        logger.info("Không tìm thấy video nào với query: " + username);
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Lỗi khi tìm video của kênh: " + e.getMessage(), e);
                }

                logger.warning("Không tìm thấy channel ID cho username: " + username + " sau khi thử tất cả các phương pháp");
            } // Xử lý URL dạng channel/ID
            else if (channelUrl.contains("/channel/")) {
                String channelId = channelUrl.substring(channelUrl.lastIndexOf("/channel/") + 9);
                // Loại bỏ các tham số query nếu có
                if (channelId.contains("?")) {
                    channelId = channelId.substring(0, channelId.indexOf("?"));
                }
                // Loại bỏ dấu / nếu có ở cuối
                if (channelId.endsWith("/")) {
                    channelId = channelId.substring(0, channelId.length() - 1);
                }
                logger.info("Đã tìm thấy channel ID từ URL: " + channelId);
                return channelId;
            } // Xử lý URL dạng user/username
            else if (channelUrl.contains("/user/")) {
                String username = channelUrl.substring(channelUrl.lastIndexOf("/user/") + 6);
                // Loại bỏ các tham số query nếu có
                if (username.contains("?")) {
                    username = username.substring(0, username.indexOf("?"));
                }
                // Loại bỏ dấu / nếu có ở cuối
                if (username.endsWith("/")) {
                    username = username.substring(0, username.length() - 1);
                }
                logger.info("Phát hiện URL loại user/username: " + username);

                // Gọi API để tìm channel ID từ username
                try {
                    YouTube.Channels.List request = youTube.channels()
                            .list("id")
                            .setForUsername(username)
                            .setKey(apiKey);

                    ChannelListResponse response = request.execute();

                    if (response.getItems() != null && !response.getItems().isEmpty()) {
                        String channelId = response.getItems().get(0).getId();
                        logger.info("Đã tìm thấy channel ID từ username: " + channelId);
                        return channelId;
                    } else {
                        logger.info("Không tìm thấy kênh với forUsername: " + username);
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Lỗi khi tìm kênh bằng username: " + e.getMessage(), e);
                }
            }

            // Phương pháp cuối cùng: Sử dụng tìm kiếm trực tiếp
            try {
                String searchTerm = channelUrl;
                // Trích xuất phần cuối của URL làm search term
                if (channelUrl.contains("/@")) {
                    searchTerm = channelUrl.substring(channelUrl.lastIndexOf("/@") + 2);
                    if (searchTerm.contains("?")) {
                        searchTerm = searchTerm.substring(0, searchTerm.indexOf("?"));
                    }
                    if (searchTerm.endsWith("/")) {
                        searchTerm = searchTerm.substring(0, searchTerm.length() - 1);
                    }
                }

                logger.info("Tìm kiếm cuối cùng với query: " + searchTerm);
                YouTube.Search.List searchRequest = youTube.search()
                        .list("snippet")
                        .setQ(searchTerm)
                        .setType("channel")
                        .setMaxResults(1L)
                        .setKey(apiKey);

                SearchListResponse searchResponse = searchRequest.execute();
                if (searchResponse.getItems() != null && !searchResponse.getItems().isEmpty()) {
                    String channelId = searchResponse.getItems().get(0).getId().getChannelId();
                    logger.info("Đã tìm thấy channel ID qua tìm kiếm cuối cùng: " + channelId);
                    return channelId;
                } else {
                    logger.info("Không tìm thấy kênh nào với tìm kiếm cuối cùng");
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Lỗi trong phương pháp tìm kiếm cuối cùng: " + e.getMessage(), e);
            }

            logger.warning("Không tìm thấy channel ID từ URL: " + channelUrl + " sau khi thử tất cả các cách");
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi không xác định khi tìm channel ID từ URL: " + channelUrl, e);
            throw new IOException("Không thể xác định channel ID: " + e.getMessage(), e);
        }
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

        if (searchResponse.getItems() == null || searchResponse.getItems().isEmpty()) {
            logger.warning("Không tìm thấy video nào cho kênh: " + channelId);
            return new ArrayList<>();
        }

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
