# Hướng dẫn triển khai Backend lên Render.com

## Bước 1: Đăng ký tài khoản Render

1. Truy cập [Render.com](https://render.com) và đăng ký tài khoản mới
2. Xác minh email nếu cần

## Bước 2: Chuẩn bị code

1. Đảm bảo rằng bạn đã đưa code lên GitHub hoặc GitLab
2. Kiểm tra các file cần thiết:
   - `pom.xml` - File cấu hình Maven
   - `system.properties` - Chỉ định phiên bản Java (java.runtime.version=11)
   - `src/main/resources/application-prod.properties` - Cấu hình cho môi trường production

## Bước 3: Tạo Web Service mới trên Render

1. Đăng nhập vào [Render Dashboard](https://dashboard.render.com)
2. Nhấn nút "New" > "Web Service"
3. Kết nối với GitHub hoặc GitLab và chọn repository
4. Điền thông tin cấu hình:
   - **Name**: `youtube-analyzer-backend`
   - **Region**: Chọn vùng gần với người dùng của bạn
   - **Branch**: `main` (hoặc branch bạn muốn deploy)
   - **Root Directory**: (để trống nếu code ở thư mục gốc)
   - **Runtime**: `Java`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar -Dspring.profiles.active=prod target/youtube-analyzer-0.0.1-SNAPSHOT.jar`

## Bước 4: Cấu hình biến môi trường

Trong phần "Environment", thêm các biến môi trường sau:

- `YOUTUBE_API_KEY`: API key YouTube của bạn
- `OPENAI_API_KEY`: API key OpenAI của bạn
- `SPRING_PROFILES_ACTIVE`: `prod`

## Bước 5: Cấu hình Advanced

1. Trong phần "Advanced", có thể điều chỉnh:
   - **Instance Type**: Start với Free tier (512 MB)
   - **Auto-Deploy**: Yes (mỗi khi push code lên branch đã chọn)
   - **Health Check Path**: `/actuator/health` (nếu bạn đã cấu hình Spring Boot Actuator)

## Bước 6: Tạo và đợi deploy

1. Nhấn nút "Create Web Service"
2. Render sẽ clone repository và bắt đầu quá trình build + deploy
3. Quá trình này có thể mất 5-10 phút

## Bước 7: Kiểm tra và cấu hình frontend

1. Sau khi deploy thành công, Render sẽ cung cấp một URL (vd: https://youtube-analyzer-backend.onrender.com)
2. Kiểm tra API bằng cách truy cập `/actuator/health` (nếu đã cấu hình) hoặc thử endpoint khác
3. Cập nhật API_BASE_URL trong frontend để trỏ đến URL này:
   ```javascript
   const API_BASE_URL =
     "https://youtube-analyzer-backend.onrender.com/api/youtube";
   ```

## Lưu ý quan trọng

- **Thời gian ngủ**: Dịch vụ miễn phí của Render sẽ "ngủ" sau 15 phút không hoạt động. Request đầu tiên có thể mất 30-45 giây để khởi động lại dịch vụ.
- **Giới hạn miễn phí**:
  - 750 giờ hoạt động/tháng
  - 100 GB băng thông/tháng
  - Tắt sau 15 phút không hoạt động
- **Nâng cấp**: Cân nhắc dùng gói trả phí nếu muốn dịch vụ luôn hoạt động

## Khắc phục sự cố

1. **Lỗi build**:

   - Kiểm tra logs trong phần "Build" của Render dashboard
   - Đảm bảo file `system.properties` chỉ định đúng phiên bản Java (11)

2. **Lỗi khi chạy**:

   - Kiểm tra logs trong phần "Logs" của Render dashboard
   - Xác minh rằng các biến môi trường đã được cấu hình đúng

3. **Lỗi kết nối từ frontend**:
   - Kiểm tra cấu hình CORS trong `application-prod.properties`
   - Đảm bảo domain frontend đã được thêm vào danh sách cho phép
