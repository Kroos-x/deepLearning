package com.yc.common.config.webMvc;

import com.yc.common.properties.UploadProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 功能描述:Spring Boot 2.0 跨域相关
 *
 * @Author: xieyc
 * @Date: 2019-09-27
 * @Version: 1.0.0
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfigurerSelf implements WebMvcConfigurer {

    private final UploadProperties uploadProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 页面相关的静态目录
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        // 映射本地存储路径 "file:D:/upload/"
        registry.addResourceHandler(uploadProperties.getImgAccessPath()).addResourceLocations("file:" + uploadProperties.getImgSavePath());
        registry.addResourceHandler(uploadProperties.getFileAccessPath()).addResourceLocations("file:" + uploadProperties.getFileSavePath());
        // swagger + knif4j
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        // swagger
        // registry.addResourceHandler("swagger-ui.html")
        //         .addResourceLocations("classpath:/META-INF/resources/");
        // registry.addResourceHandler("/webjars/**")
        //         .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 跨域设置
     *
     * @return source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, new MappingJackson2HttpMessageConverter());
    }
}
