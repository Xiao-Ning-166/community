package edu.hue.community.controller.advice;

import edu.hue.community.util.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author 47552
 * @date 2021/09/19
 * 统一处理异常
 */
@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    @ExceptionHandler
    public void handlerException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("服务器发生错误，原因：" + e.getMessage());
        StackTraceElement[] stackTrace = e.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            log.error(stackTraceElement.toString());
        }
        String header = request.getHeader("X-Requested-With");
        // 判断请求的类型
        if ("XMLHttpRequest".equals(header)) {
            // 请求为异步请求
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(JSONUtils.getJSONString(600,"服务器出现异常！！！"));
        } else {
            // 请求为http请求
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }

}
