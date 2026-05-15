package work.chncyl.base.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import work.chncyl.base.security.interceptor.CustomAccessInterceptor;

/**
 * Web MVC 配置类，用于注册拦截器
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final CustomAccessInterceptor customAccessInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册自定义访问拦截器
        registry.addInterceptor(customAccessInterceptor)
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns(    // 排除静态资源等
                        "/**.html",
                        "/**.css",
                        "/**.js",
                        "/favicon.ico",
                        "/webjars/**"
                );
    }
}
