document.addEventListener("DOMContentLoaded", function () {
  console.log("DOM fully loaded and parsed. Initializing script...");

  // Elements
  const analyzeForm = document.getElementById("analyzeForm");
  const channelUrlInput = document.getElementById("channelUrl");
  const loadingIndicator = document.getElementById("loadingIndicator");
  const errorMessage = document.getElementById("errorMessage");
  const resultsContainer = document.getElementById("resultsContainer");
  const videoResults = document.getElementById("videoResults");
  const videoCardTemplate = document.getElementById("videoCardTemplate");

  // System log elements - with checks
  const systemLogContainer = document.getElementById("systemLogContainer");
  const environmentInfo = document.getElementById("environmentInfo");
  const hostnameInfo = document.getElementById("hostnameInfo");
  const apiUrlInfo = document.getElementById("apiUrlInfo");
  const apiStatusInfo = document.getElementById("apiStatusInfo");
  const toggleLogBtn = document.getElementById("toggleLogBtn");

  console.log("System Log Elements Check:");
  console.log({
    systemLogContainer,
    environmentInfo,
    hostnameInfo,
    apiUrlInfo,
    apiStatusInfo,
    toggleLogBtn,
  });

  if (!systemLogContainer) {
    console.error(
      "FATAL: systemLogContainer not found. UI will not be updated for system logs."
    );
    // Early exit or handle gracefully if system log is critical
  }
  if (!environmentInfo) console.warn("environmentInfo element not found.");
  if (!hostnameInfo) console.warn("hostnameInfo element not found.");
  if (!apiUrlInfo) console.warn("apiUrlInfo element not found.");
  if (!apiStatusInfo) console.warn("apiStatusInfo element not found.");
  if (!toggleLogBtn) console.warn("toggleLogBtn element not found.");

  // API URL - Tự động chọn dựa trên môi trường
  const config = {
    development: {
      apiUrl: "http://localhost:8080/api/youtube",
    },
    production: {
      apiUrl: "https://youtube-5cvw.onrender.com/api/youtube",
    },
  };

  const isProduction =
    window.location.hostname !== "localhost" &&
    window.location.hostname !== "127.0.0.1";
  const API_BASE_URL = isProduction
    ? config.production.apiUrl
    : config.development.apiUrl;

  // Cập nhật thông tin hệ thống trên UI
  function updateSystemInfo() {
    console.log("Attempting to update system info in UI...");
    if (environmentInfo)
      environmentInfo.textContent = isProduction ? "Production" : "Development";
    if (hostnameInfo) hostnameInfo.textContent = window.location.hostname;
    if (apiUrlInfo) apiUrlInfo.textContent = API_BASE_URL;

    if (apiStatusInfo) {
      apiStatusInfo.textContent = "Đang kiểm tra...";
      apiStatusInfo.className = "badge bg-warning"; // Reset and indicate loading
    }

    // Kiểm tra trạng thái API với timeout
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 5000); // 5 giây timeout

    fetch(`${API_BASE_URL}/health`, {
      method: "GET",
      signal: controller.signal,
      // Tăng tốc kết nối bằng cache: no-store để không lưu cache
      cache: "no-store",
      // Tăng tốc kết nối bằng mode: cors để sử dụng CORS
      mode: "cors",
      // Headers đảm bảo request nhỏ và nhanh
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
      },
    })
      .then((response) => {
        clearTimeout(timeoutId);
        if (apiStatusInfo) {
          if (response.ok) {
            apiStatusInfo.textContent = "Hoạt động";
            apiStatusInfo.className = "badge bg-success";
          } else {
            apiStatusInfo.textContent = `Lỗi kết nối (${response.status})`;
            apiStatusInfo.className = "badge bg-danger";
          }
        }
        console.log("API Health check response:", response.status);
      })
      .catch((error) => {
        clearTimeout(timeoutId);
        if (apiStatusInfo) {
          if (error.name === "AbortError") {
            apiStatusInfo.textContent = "Timeout - Kết nối quá lâu";
          } else {
            apiStatusInfo.textContent = "Không thể kết nối";
          }
          apiStatusInfo.className = "badge bg-danger";
        }
        console.error("API Health check failed:", error);
      });
  }

  // Khởi tạo thông tin hệ thống và hiển thị khung log
  if (systemLogContainer) {
    console.log(
      "systemLogContainer found. Making it visible and updating info."
    );
    systemLogContainer.classList.remove("d-none"); // Ensure it's visible
    updateSystemInfo(); // Populate with info

    if (toggleLogBtn) {
      toggleLogBtn.addEventListener("click", function () {
        systemLogContainer.classList.toggle("d-none");
        console.log(
          "Toggle log button clicked. systemLogContainer classes:",
          systemLogContainer.className
        );
      });
    } else {
      console.warn("toggleLogBtn not found, log toggling will not work.");
    }
  } else {
    console.error(
      "systemLogContainer NOT FOUND. Cannot initialize system log UI."
    );
  }

  console.log(
    "Môi trường (determined by script):",
    isProduction ? "Production" : "Development"
  );
  console.log("API URL được sử dụng:", API_BASE_URL);

  // Format date
  function formatDate(dateString) {
    const options = { year: "numeric", month: "short", day: "numeric" };
    return new Date(dateString).toLocaleDateString("vi-VN", options);
  }

  // Format numbers
  function formatNumber(num) {
    return new Intl.NumberFormat("vi-VN").format(num);
  }

  // Chuẩn hóa URL YouTube
  function normalizeYouTubeUrl(url) {
    console.log("URL gốc:", url);
    if (!url.startsWith("http")) {
      if (!url.includes("youtube.com") && !url.includes("youtu.be")) {
        url = "https://www.youtube.com/" + url;
      } else {
        url = "https://" + url;
      }
    }
    if (
      url.toLowerCase().includes("web5ngay") ||
      url.toLowerCase().includes("web 5 ngay") ||
      url.includes("@Web5Ngay")
    ) {
      console.log("Đã phát hiện kênh Web5ngay, chuyển sang URL chính thức");
      url = "https://www.youtube.com/@Web5Ngay";
    }
    console.log("URL sau khi chuẩn hóa:", url);
    return url;
  }

  // Create video card
  function createVideoCard(video) {
    const card = videoCardTemplate.content.cloneNode(true);
    const thumbnail = card.querySelector(".video-thumbnail");
    thumbnail.src = `https://i.ytimg.com/vi/${video.videoId}/mqdefault.jpg`;
    card.querySelector(".video-title").textContent = video.title;
    card.querySelector(".video-views").textContent = formatNumber(
      video.viewCount
    );
    card.querySelector(".video-likes").textContent = formatNumber(
      video.likeCount
    );
    card.querySelector(".video-date").textContent = formatDate(
      video.publishedAt
    );
    card.querySelector(".video-summary").textContent = video.summary;
    const videoLink = card.querySelector(".video-link");
    videoLink.href = `https://www.youtube.com/watch?v=${video.videoId}`;
    return card;
  }

  // Show error message
  function showError(message) {
    if (errorMessage) {
      errorMessage.textContent = message;
      errorMessage.classList.remove("d-none");
    } else {
      console.error(
        "errorMessage element not found, cannot display error UI:",
        message
      );
    }
    if (loadingIndicator) loadingIndicator.classList.add("d-none");
    if (resultsContainer) resultsContainer.classList.add("d-none");
  }

  // Handle form submission
  if (analyzeForm) {
    analyzeForm.addEventListener("submit", function (e) {
      e.preventDefault();
      console.log("----------------------");
      console.log("THÔNG TIN API REQUEST (FORM SUBMIT):");
      console.log("Môi trường:", isProduction ? "Production" : "Development");
      console.log("API URL gốc:", API_BASE_URL);
      console.log("----------------------");

      const channelUrl = channelUrlInput ? channelUrlInput.value.trim() : "";
      if (!channelUrl) {
        showError("Vui lòng nhập URL kênh YouTube.");
        return;
      }

      const normalizedUrl = normalizeYouTubeUrl(channelUrl);
      console.log("Gửi yêu cầu phân tích kênh:", normalizedUrl);

      if (apiStatusInfo) {
        apiStatusInfo.textContent = "Đang gửi yêu cầu...";
        apiStatusInfo.className = "badge bg-warning";
      }

      if (loadingIndicator) loadingIndicator.classList.remove("d-none");
      if (errorMessage) errorMessage.classList.add("d-none");
      if (resultsContainer) resultsContainer.classList.add("d-none");

      const apiUrl = `${API_BASE_URL}/analyze`;
      console.log("Gọi API tại URL:", apiUrl);

      // Tạo controller để có thể hủy request nếu quá lâu
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 30000); // 30 giây timeout

      fetch(apiUrl, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
        },
        body: JSON.stringify({ channelUrl: normalizedUrl }),
        signal: controller.signal,
        cache: "no-store",
        mode: "cors",
      })
        .then((response) => {
          clearTimeout(timeoutId);

          console.log("Mã trạng thái phản hồi:", response.status);
          console.log("URL API được gọi:", apiUrl);
          console.log(
            "Request body:",
            JSON.stringify({ channelUrl: normalizedUrl })
          );

          if (apiStatusInfo) {
            if (response.ok) {
              apiStatusInfo.textContent = `Hoạt động (Mã: ${response.status})`;
              apiStatusInfo.className = "badge bg-success";
            } else {
              apiStatusInfo.textContent = `Lỗi (Mã: ${response.status})`;
              apiStatusInfo.className = "badge bg-danger";
            }
          }

          if (!response.ok) {
            return response.json().then((data) => {
              console.error("Lỗi từ server:", data);
              throw new Error(
                data.error || "Có lỗi xảy ra khi phân tích kênh YouTube."
              );
            });
          }
          return response.json();
        })
        .then((videos) => {
          if (loadingIndicator) loadingIndicator.classList.add("d-none");
          console.log("Đã nhận được", videos.length, "video từ API");
          if (videoResults) videoResults.innerHTML = "";

          if (videos.length === 0) {
            showError("Không tìm thấy video nào từ kênh này.");
            return;
          }

          videos.forEach((video) => {
            if (videoResults) videoResults.appendChild(createVideoCard(video));
          });

          if (resultsContainer) resultsContainer.classList.remove("d-none");
        })
        .catch((error) => {
          console.error("Lỗi fetch:", error);
          clearTimeout(timeoutId); // Đảm bảo xóa timeout

          if (apiStatusInfo) {
            apiStatusInfo.textContent = "Lỗi phân tích";
            apiStatusInfo.className = "badge bg-danger";
          }

          if (error.name === "AbortError") {
            showError(
              "Yêu cầu bị hủy do quá thời gian chờ (30 giây). Server có thể đang quá tải hoặc ở chế độ ngủ. Vui lòng thử lại sau."
            );
            console.log("Request timed out after 30 seconds");
            return;
          }

          if (error.message === "Failed to fetch") {
            let errorMsg = `Không thể kết nối đến server API tại ${API_BASE_URL}. `;
            if (!isProduction) {
              errorMsg +=
                "Kiểm tra xem server backend đã chạy chưa (localhost:8080).";
            } else {
              errorMsg += "Vui lòng thử lại sau hoặc liên hệ quản trị viên.";
            }
            showError(errorMsg);
          } else {
            showError(
              error.message || "Có lỗi xảy ra khi phân tích kênh YouTube."
            );
          }
        });
    });
  } else {
    console.error("analyzeForm not found. Form submission will not work.");
  }
});
