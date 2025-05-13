document.addEventListener("DOMContentLoaded", function () {
  // Elements
  const analyzeForm = document.getElementById("analyzeForm");
  const channelUrlInput = document.getElementById("channelUrl");
  const loadingIndicator = document.getElementById("loadingIndicator");
  const errorMessage = document.getElementById("errorMessage");
  const resultsContainer = document.getElementById("resultsContainer");
  const videoResults = document.getElementById("videoResults");
  const videoCardTemplate = document.getElementById("videoCardTemplate");

  // API URL - change this to your actual backend URL when deployed
  const API_BASE_URL = "http://localhost:8080/api/youtube";

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

    // Xử lý URL không có protocol
    if (!url.startsWith("http")) {
      if (!url.includes("youtube.com") && !url.includes("youtu.be")) {
        url = "https://www.youtube.com/" + url;
      } else {
        url = "https://" + url;
      }
    }

    // Xử lý đặc biệt cho Web5ngay
    if (
      url.toLowerCase().includes("web5ngay") ||
      url.toLowerCase().includes("web 5 ngay") ||
      url.includes("@Web5Ngay")
    ) {
      console.log("Đã phát hiện kênh Web5ngay, chuyển sang URL chính thức");
      // URL chính thức của kênh Web5ngay (đúng là không có số 5 ở cuối)
      url = "https://www.youtube.com/@Web5Ngay";
    }

    console.log("URL sau khi chuẩn hóa:", url);
    return url;
  }

  // Create video card
  function createVideoCard(video) {
    const card = videoCardTemplate.content.cloneNode(true);

    // Set video thumbnail
    const thumbnail = card.querySelector(".video-thumbnail");
    thumbnail.src = `https://i.ytimg.com/vi/${video.videoId}/mqdefault.jpg`;

    // Set video details
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

    // Set video link
    const videoLink = card.querySelector(".video-link");
    videoLink.href = `https://www.youtube.com/watch?v=${video.videoId}`;

    return card;
  }

  // Show error message
  function showError(message) {
    errorMessage.textContent = message;
    errorMessage.classList.remove("d-none");
    loadingIndicator.classList.add("d-none");
    resultsContainer.classList.add("d-none");
  }

  // Handle form submission
  analyzeForm.addEventListener("submit", function (e) {
    e.preventDefault();

    const channelUrl = channelUrlInput.value.trim();
    if (!channelUrl) {
      showError("Vui lòng nhập URL kênh YouTube.");
      return;
    }

    // Chuẩn hóa URL
    const normalizedUrl = normalizeYouTubeUrl(channelUrl);
    console.log("Gửi yêu cầu phân tích kênh:", normalizedUrl);

    // Show loading indicator
    loadingIndicator.classList.remove("d-none");
    errorMessage.classList.add("d-none");
    resultsContainer.classList.add("d-none");

    // Call API to analyze channel
    fetch(`${API_BASE_URL}/analyze`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ channelUrl: normalizedUrl }),
    })
      .then((response) => {
        console.log("Mã trạng thái phản hồi:", response.status);
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
        // Hide loading indicator
        loadingIndicator.classList.add("d-none");

        // Log số lượng video
        console.log("Đã nhận được", videos.length, "video từ API");

        // Clear previous results
        videoResults.innerHTML = "";

        // Check if there are videos
        if (videos.length === 0) {
          showError("Không tìm thấy video nào từ kênh này.");
          return;
        }

        // Display videos
        videos.forEach((video) => {
          videoResults.appendChild(createVideoCard(video));
        });

        // Show results container
        resultsContainer.classList.remove("d-none");
      })
      .catch((error) => {
        console.error("Lỗi:", error);
        showError(error.message || "Có lỗi xảy ra khi phân tích kênh YouTube.");
      });
  });
});
