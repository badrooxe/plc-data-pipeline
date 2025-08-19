// package com.plcpipeline.ingestion.configs;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration
// public class CorsConfig {

//     @Bean
//     public WebMvcConfigurer corsConfigurer() {
//         return new WebMvcConfigurer() {
//             @SuppressWarnings("null")
//             @Override
//             public void addCorsMappings(CorsRegistry registry) {
//                 registry.addMapping("/api/**") // all API paths
//                         .allowedOrigins("http://localhost:4200") // angular dev server
//                         .allowedMethods("GET", "POST", "PUT", "DELETE") // Allowed HTTP methods
//                         .allowedHeaders("*") // Allowed headers
//                         .allowCredentials(true);
//             }
//         };
//     }
// }
