package edu.hue.community.controller.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.hue.community.entity.LoginTicket;
import edu.hue.community.entity.User;
import edu.hue.community.service.LoginTicketService;
import edu.hue.community.service.UserService;
import edu.hue.community.util.CookieUtils;
import edu.hue.community.util.HostHolder;
import edu.hue.community.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author 47552
 * @date 2021/09/16
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private LoginTicketService loginTicketService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 在 Controller 方法执行之前执行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从Cookie中获取凭证
        String ticket = CookieUtils.getCookieValue(request, "ticket");
        if (ticket != null) {
            // 查询凭证
            //QueryWrapper query = new QueryWrapper();
            //query.eq("ticket", ticket);
            //LoginTicket loginTicket = loginTicketService.getOne(query);
            String loginTicketKey = RedisUtils.getLoginTicketKey(ticket);
            LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(loginTicketKey);
            // 检查凭证是否有效
            if (loginTicket != null
                    && loginTicket.getStatus() == 0
                    && loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询用户
                User user = userService.getUserById(loginTicket.getUserId());
                //
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    /**
     * 在 Controller 方法执行之后，模板引擎之前执行
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    /**
     * 在 模板引擎 之后执行
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
