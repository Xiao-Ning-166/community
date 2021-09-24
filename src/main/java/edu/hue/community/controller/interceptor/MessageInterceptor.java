package edu.hue.community.controller.interceptor;

import edu.hue.community.entity.User;
import edu.hue.community.service.MessageService;
import edu.hue.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 47552
 * @date 2021/09/23
 * 统计未读消息数量的拦截器
 */
@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            Integer letterUnreadCount = messageService.getLetterUnreadCount(user.getId(), null);
            Integer noticeUnreadCount = messageService.getNoticeUnreadCount(user.getId(), null);
            modelAndView.addObject("unreadTotal",letterUnreadCount+noticeUnreadCount);
        }
    }
}
