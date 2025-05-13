package com.example.youtubeanalyzer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "https://youtube-vbch-git-main-hieuthu16s-projects.vercel.app",
                        "https://youtube-vbch.vercel.app",
                        "http://localhost:3000",
                        "http://localhost:8080",
                        "http://localhost:5173",
                        "http://127.0.0.1:3000",
                        "http://127.0.0.1:8080",
                        "http://127.0.0.1:5173"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Methods",
                        "Access-Control-Allow-Headers", "Access-Control-Max-Age")
                .allowCredentials(true)
                .maxAge(3600);
    }

    // Thêm filter CORS để đảm bảo nó được áp dụng cho tất cả các yêu cầu
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Cho phép tất cả các origins
        config.addAllowedOrigin("https://youtube-vbch-git-main-hieuthu16s-projects.vercel.app");
        config.addAllowedOrigin("https://youtube-vbch.vercel.app");
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("http://127.0.0.1:3000");
        config.addAllowedOrigin("http://127.0.0.1:8080");
        config.addAllowedOrigin("http://127.0.0.1:5173");

        // Cho phép tất cả các headers và methods
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
