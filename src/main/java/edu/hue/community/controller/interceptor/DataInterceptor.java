package edu.hue.community.controller.interceptor;

import edu.hue.community.entity.User;
import edu.hue.community.service.DataService;
import edu.hue.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 47552
 * @date 2021/10/01
 */
@Component
public class DataInterceptor implements HandlerInterceptor {

    @Autowired
    private DataService dataService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 得到ip地址
        String ip = request.getRemoteHost();
        // 保存uv
        dataService.saveUV(ip);

        // 判断用户是否登录
        User user = hostHolder.getUser();
        if (user != null) {
            dataService.saveDAU(user.getId());
        }
        return true;
    }
}
