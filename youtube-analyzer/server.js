const http = require("http");
const fs = require("fs");
const path = require("path");

const PORT = 3000;

const MIME_TYPES = {
  ".html": "text/html",
  ".css": "text/css",
  ".js": "text/javascript",
  ".json": "application/json",
  ".png": "image/png",
  ".jpg": "image/jpeg",
  ".jpeg": "image/jpeg",
  ".gif": "image/gif",
};

const server = http.createServer((req, res) => {
  console.log(`Request: ${req.url}`);

  // Xử lý URL để lấy đường dẫn tệp
  let filePath = req.url;
  if (filePath === "/") {
    filePath = "/index.html";
  }

  filePath = path.join(__dirname, filePath);

  // Lấy phần mở rộng của tệp
  const extname = path.extname(filePath);

  // Xác định loại nội dung
  const contentType = MIME_TYPES[extname] || "application/octet-stream";

  // Đọc tệp
  fs.readFile(filePath, (error, content) => {
    if (error) {
      if (error.code === "ENOENT") {
        // Tệp không tồn tại
        fs.readFile(path.join(__dirname, "404.html"), (err, data) => {
          res.writeHead(404, { "Content-Type": "text/html" });
          res.end(data || "Không tìm thấy trang 404", "utf-8");
        });
      } else {
        // Lỗi máy chủ
        res.writeHead(500);
        res.end(`Lỗi máy chủ: ${error.code}`);
      }
    } else {
      // Thành công
      res.writeHead(200, { "Content-Type": contentType });
      res.end(content, "utf-8");
    }
  });
});

server.listen(PORT, () => {
  console.log(`Server đang chạy tại http://localhost:${PORT}`);
});
