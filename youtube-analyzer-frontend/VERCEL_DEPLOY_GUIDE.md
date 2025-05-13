# Hướng dẫn chi tiết deploy YouTube Analyzer lên Vercel

## 1. Chuẩn bị

### 1.1 Tạo GitHub Repository

1. Truy cập [GitHub](https://github.com) và đăng nhập
2. Nhấn nút "New" để tạo repository mới
3. Đặt tên: "youtube-analyzer-frontend"
4. Chọn "Public"
5. Nhấn "Create repository"

### 1.2 Đẩy code lên GitHub

Từ thư mục `youtube-analyzer-frontend` của bạn, chạy các lệnh sau:

```bash
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/YOUR_USERNAME/youtube-analyzer-frontend.git
git push -u origin main
```

## 2. Đăng ký và đăng nhập Vercel

### 2.1 Tạo tài khoản Vercel

1. Truy cập [Vercel](https://vercel.com/signup)
2. Chọn "Continue with GitHub" để tạo tài khoản bằng GitHub
3. Cho phép Vercel truy cập vào tài khoản GitHub của bạn

### 2.2 Đăng nhập vào Vercel Dashboard

Sau khi đăng ký, bạn sẽ được chuyển đến [Dashboard](https://vercel.com/dashboard)

## 3. Deploy project lên Vercel (Chi tiết từng bước)

### 3.1 Import repository

1. Từ Dashboard, nhấn nút "Add New..." > "Project"
2. Trong phần "Import Git Repository", tìm và chọn `youtube-analyzer-frontend`
3. Nhấn "Import"

### 3.2 Cấu hình project

Bạn sẽ thấy màn hình "Configure Project" với các tùy chọn sau:

1. **Project Name**:

   - Để mặc định `youtube-analyzer-frontend` hoặc thay đổi tùy ý

2. **Framework Preset**:

   - Chọn "Other" từ dropdown menu

3. **Root Directory**:

   - Để trống (vì code nằm ở thư mục gốc)

4. **Build and Output Settings**:

   - Build Command: Để trống (không cần build vì là HTML tĩnh)
   - Output Directory: Để trống (mặc định)

5. **Environment Variables**:
   - Không cần thêm biến môi trường nào

### 3.3 Deploy

1. Kiểm tra lại tất cả cài đặt
2. Nhấn nút "Deploy"
3. Đợi Vercel hoàn thành quá trình deploy (thường mất 1-2 phút)

### 3.4 Kiểm tra

1. Khi deploy hoàn tất, Vercel sẽ hiển thị thông báo "Congratulations!"
2. Nhấn vào URL được cung cấp (thường là `https://youtube-analyzer-frontend.vercel.app`)
3. Kiểm tra xem trang web đã hoạt động chưa

## 4. Cấu hình Domain (Tùy chọn)

### 4.1 Thêm Custom Domain

1. Từ dashboard của project, chọn tab "Settings" > "Domains"
2. Nhập tên miền của bạn và nhấn "Add"
3. Làm theo hướng dẫn để cấu hình DNS

## 5. Kết nối với Backend

### 5.1 Deploy Backend

Trước khi tiếp tục, hãy đảm bảo bạn đã deploy backend lên Render.com theo hướng dẫn trong file `DEPLOY.md` của backend.

### 5.2 Cập nhật API URL

1. Từ dashboard, chọn "Settings" > "Git"
2. Quay lại repository GitHub của bạn
3. Chỉnh sửa file `js/main.js`:
   ```javascript
   const API_BASE_URL =
     "https://youtube-analyzer-backend.onrender.com/api/youtube";
   ```
4. Commit và push thay đổi lên GitHub
5. Vercel sẽ tự động deploy lại project với URL API mới

## 6. Các tính năng bổ sung của Vercel

### 6.1 Analytics

1. Từ dashboard của project, chọn tab "Analytics"
2. Kích hoạt Web Analytics để theo dõi lưu lượng truy cập

### 6.2 Tích hợp với GitHub

Vercel tự động cập nhật khi bạn push code mới lên GitHub:

1. Mỗi pull request tạo một "Preview Deployment"
2. Mỗi commit vào branch chính tạo một "Production Deployment"

### 6.3 Rollback

Nếu có vấn đề với bản deploy mới:

1. Từ dashboard của project, chọn tab "Deployments"
2. Tìm bản deploy trước đó hoạt động tốt
3. Nhấn vào 3 dấu chấm (...) > "Promote to Production"

## 7. Xử lý sự cố

### 7.1 Lỗi deploy

1. Kiểm tra build logs từ Vercel dashboard
2. Đảm bảo cấu trúc project và `vercel.json` đã đúng

### 7.2 Lỗi kết nối API

1. Mở Console trong Developer Tools của trình duyệt
2. Kiểm tra lỗi CORS hoặc kết nối
3. Xác minh backend đã được cấu hình để chấp nhận request từ Vercel domain

### 7.3 Trợ giúp

Nếu gặp vấn đề, tham khảo:

- [Vercel Documentation](https://vercel.com/docs)
- [Vercel Support](https://vercel.com/support)

## 8. Tài nguyên bổ sung

- [Vercel CLI](https://vercel.com/docs/cli) - Công cụ dòng lệnh cho Vercel
- [Next.js](https://nextjs.org/) - Framework thay thế cho frontend tĩnh
- [Vercel Pricing](https://vercel.com/pricing) - Kế hoạch nâng cấp
