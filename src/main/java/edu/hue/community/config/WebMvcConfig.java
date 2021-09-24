package edu.hue.community.config;

import edu.hue.community.controller.interceptor.LoginRequiredInterceptor;
import edu.hue.community.controller.interceptor.LoginTicketInterceptor;
import edu.hue.community.controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 47552
 * @date 2021/09/16
 */
@Configuration
public class WebMvcConfig  implements WebMvcConfigurer {

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    private MessageInterceptor messageInterceptor;

    /**
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加检查凭证拦截器
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/css/**", "/img/**", "/js/**");
        // 添加检查登录状态拦截器
        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/css/**", "/img/**", "/js/**");
        // 添加显示未读消息数量的拦截器
        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/css/**", "/img/**", "/js/**");
    }
}
