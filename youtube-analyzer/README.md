# YouTube Analyzer

Ứng dụng phân tích kênh YouTube với Spring Boot và JavaScript, trả về số lượt like, view và tóm tắt video bằng GPT.

## Tính năng

- Nhập URL kênh YouTube và phân tích 5 video mới nhất
- Hiển thị số lượt xem, lượt thích của mỗi video
- Tạo bản tóm tắt tự động cho mỗi video bằng OpenAI GPT
- Lưu kết quả vào cơ sở dữ liệu để tham khảo sau
- Hỗ trợ đặc biệt cho một số kênh cụ thể (như Web5ngay)

## Yêu cầu

- Java 11+
- Maven
- YouTube Data API key
- OpenAI API key

## Cài đặt

1. Clone repository này về máy của bạn
2. Cập nhật API key trong file `src/main/resources/application.properties`:

```properties
youtube.api.key=YOUR_YOUTUBE_API_KEY
openai.api.key=YOUR_OPENAI_API_KEY
```

3. Biên dịch và chạy ứng dụng:

```bash
cd youtube-analyzer
mvn spring-boot:run
```

4. Chạy frontend (Node.js):

```bash
cd youtube-analyzer
node server.js
```

5. Truy cập ứng dụng web tại địa chỉ: http://localhost:3000 hoặc mở trực tiếp file `frontend/html/index.html`

## Cấu trúc dự án

```
youtube-analyzer/
├── backend/
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/
│       │   │       └── example/
│       │   │           └── youtubeanalyzer/
│       │   │               ├── controller/
│       │   │               ├── service/
│       │   │               ├── model/
│       │   │               ├── repository/
│       │   │               └── config/
│       │   └── resources/
│       └── test/
└── frontend/
    ├── html/
    ├── css/
    └── js/
```

## Cách sử dụng

1. Mở trình duyệt và truy cập http://localhost:3000 hoặc mở trực tiếp file frontend/html/index.html
2. Nhập URL kênh YouTube (ví dụ: https://www.youtube.com/@tenchannel)
3. Nhấn nút "Phân tích" để bắt đầu quá trình phân tích
4. Đợi một lát để hệ thống xử lý và hiển thị kết quả
5. Xem thông tin về mỗi video bao gồm số lượt xem, lượt thích và bản tóm tắt

## Các URL được hỗ trợ đặc biệt

Ứng dụng này hỗ trợ xử lý đặc biệt cho một số kênh YouTube để đảm bảo phân tích chính xác:

- Kênh Web5ngay:
  - Nhận dạng các biến thể: "web5ngay", "Web5Ngay", "web 5 ngay", "@Web5Ngay", v.v.
  - Tự động chuyển hướng đến URL chính thức: "https://www.youtube.com/@Web5Ngay"

## API Endpoints

- `POST /api/youtube/analyze`: Phân tích kênh YouTube từ URL
- `GET /api/youtube/videos`: Lấy danh sách video đã phân tích từ một kênh

## Lưu ý

- Cần có YouTube Data API key hợp lệ. Bạn có thể tạo key tại [Google Cloud Console](https://console.cloud.google.com/)
- Cần có OpenAI API key hợp lệ. Đăng ký tại [OpenAI](https://openai.com/api/)
