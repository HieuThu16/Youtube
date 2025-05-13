#!/usr/bin/env bash
# Cài đặt JDK 11
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 11.0.15-tem

# Biên dịch và đóng gói ứng dụng
cd youtube-analyzer
./mvnw clean package -DskipTests 