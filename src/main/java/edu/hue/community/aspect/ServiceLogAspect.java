package edu.hue.community.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 47552
 * @date 2021/09/19
 * 统一记录 Service 层的日志
 */
@Component
@Aspect
@Slf4j
public class ServiceLogAspect {

    @Pointcut(value = "execution(* edu.hue.community.service.*.*(..))")
    public void pointcut() {

    }

    @Before(value = "pointcut()")
    public void before(JoinPoint joinPoint) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String ip = request.getRemoteHost();
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String method = joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName()+"()";
            log.info("用户[{}]，在[{}]，访问了[{}方法]",ip,time,method);
        }
    }

}
