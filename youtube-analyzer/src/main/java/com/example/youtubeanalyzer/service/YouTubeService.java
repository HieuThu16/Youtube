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
        logger.info("Xử lý URL kênh: " + channelUrl);

        // Xử lý đặc biệt cho kênh Web5ngay
        if (channelUrl.toLowerCase().contains("web5ngay")
                || channelUrl.toLowerCase().contains("web 5 ngay")
                || channelUrl.contains("@Web5Ngay")) {

            logger.info("Phát hiện kênh Web5ngay, sử dụng URL chính thức");
            channelUrl = "https://www.youtube.com/@Web5Ngay";

            // Thực hiện gọi API trực tiếp để lấy channel ID
            YouTube.Search.List request = youTube.search()
                    .list("snippet")
                    .setKey(apiKey)
                    .setQ("Web5ngay")
                    .setType("channel")
                    .setMaxResults(1L);

            SearchListResponse response = request.execute();
            if (response.getItems() != null && !response.getItems().isEmpty()) {
                String channelId = response.getItems().get(0).getId().getChannelId();
                logger.info("Đã tìm thấy channel ID cho Web5ngay: " + channelId);
                return channelId;
            }
        }

        // Extract username or channel ID from URL
        String username = null;
        String channelId = null;
        String customUrl = null;

        if (channelUrl.contains("/user/")) {
            logger.info("Phát hiện định dạng URL /user/");
            username = channelUrl.substring(channelUrl.indexOf("/user/") + 6);
            username = username.split("/")[0];
            logger.info("Đã trích xuất username: " + username);

            // Get channel ID from username
            YouTube.Channels.List request = youTube.channels()
                    .list("id")
                    .setKey(apiKey)
                    .setForUsername(username);

            ChannelListResponse response = request.execute();
            if (response.getItems() != null && !response.getItems().isEmpty()) {
                channelId = response.getItems().get(0).getId();
                logger.info("Đã tìm thấy channel ID từ username: " + channelId);
            } else {
                logger.warning("Không tìm thấy kênh với username: " + username);
            }
        } else if (channelUrl.contains("/channel/")) {
            logger.info("Phát hiện định dạng URL /channel/");
            channelId = channelUrl.substring(channelUrl.indexOf("/channel/") + 9);
            channelId = channelId.split("/")[0];
            logger.info("Đã trích xuất channel ID: " + channelId);
        } else if (channelUrl.contains("/@")) {
            logger.info("Phát hiện định dạng URL /@");
            String handle = channelUrl.substring(channelUrl.indexOf("/@") + 2);
            handle = handle.split("/")[0];
            logger.info("Đã trích xuất handle: " + handle);

            YouTube.Search.List request = youTube.search()
                    .list("snippet")
                    .setKey(apiKey)
                    .setQ("@" + handle)
                    .setType("channel")
                    .setMaxResults(1L);

            SearchListResponse response = request.execute();
            if (response.getItems() != null && !response.getItems().isEmpty()) {
                channelId = response.getItems().get(0).getId().getChannelId();
                logger.info("Đã tìm thấy channel ID từ handle: " + channelId);
            } else {
                logger.warning("Không tìm thấy kênh với handle: " + handle);
            }
        } else {
            // Định dạng khác (VD: youtube.com/TênKênh)
            logger.info("Định dạng URL không khớp các mẫu chuẩn, thử xử lý dạng URL tùy chỉnh");

            // Trích xuất phần tùy chỉnh từ URL
            String[] parts = channelUrl.split("/");
            if (parts.length > 3) {
                customUrl = parts[parts.length - 1];
                if (customUrl.isEmpty() && parts.length > 4) {
                    customUrl = parts[parts.length - 2]; // Trường hợp có dấu / ở cuối
                }
                logger.info("Đã trích xuất tên tùy chỉnh: " + customUrl);

                // Tìm kiếm kênh bằng tên tùy chỉnh
                YouTube.Search.List request = youTube.search()
                        .list("snippet")
                        .setKey(apiKey)
                        .setQ(customUrl)
                        .setType("channel")
                        .setMaxResults(1L);

                SearchListResponse response = request.execute();
                if (response.getItems() != null && !response.getItems().isEmpty()) {
                    channelId = response.getItems().get(0).getId().getChannelId();
                    logger.info("Đã tìm thấy channel ID từ tên tùy chỉnh: " + channelId);
                } else {
                    logger.warning("Không tìm thấy kênh với tên tùy chỉnh: " + customUrl);
                }
            } else {
                logger.warning("Không thể phân tích URL: " + channelUrl);
            }
        }

        if (channelId == null) {
            logger.warning("Không tìm được channel ID từ URL: " + channelUrl);
        } else {
            logger.info("Channel ID cuối cùng: " + channelId);
        }

        return channelId;
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
