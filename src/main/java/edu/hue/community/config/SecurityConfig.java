package edu.hue.community.config;

import edu.hue.community.util.JSONUtils;
import edu.hue.community.util.MessageConstant;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author 47552
 * @date 2021/09/27
 * Spring Security 的配置类
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 授权
        http.authorizeRequests()
            .antMatchers(
                    "/add/**", "/myReply/**",
                    "/publishPost", "/myPost/**",
                    "/follow", "/unFollow", "/listFollowee/**", "/listFollower/**",
                    "/like",
                    "/letter", "/getdetail/**", "/sendLetter", "/deleteLetter/**", "/notice/**",
                    "/setting", "/updateHeader", "/updatePassword", "/profile/**"
            )
            .hasAnyAuthority(
                    MessageConstant.AUTHORITY_ADMIN,
                    MessageConstant.AUTHORITY_USER,
                    MessageConstant.AUTHORITY_MODERATOR
            )
            .antMatchers("/top/**", "wonderful/**", "/deletePost/**")
            .hasAnyAuthority(MessageConstant.AUTHORITY_ADMIN)
            .antMatchers("/deletePost/**")
            .hasAnyAuthority(MessageConstant.AUTHORITY_MODERATOR)
            .anyRequest().permitAll();

        http.csrf().disable();

        // 权限不够时的处理
        http.exceptionHandling()
            .authenticationEntryPoint(new AuthenticationEntryPoint() {
                // 没有登录
                @Override
                public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                    String header = request.getHeader("x-requested-with");
                    if ("XMLHttpRequest".equals(header)) {
                        response.setContentType("application/plain;charset=utf-8");
                        PrintWriter writer = response.getWriter();
                        writer.write(JSONUtils.getJSONString(403, "您还没有登录！！！"));
                    } else {
                        response.sendRedirect(request.getContextPath() + "/login");
                    }
                }
            })
            .accessDeniedHandler(new AccessDeniedHandler() {
                // 没有权限
                @Override
                public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                    String header = request.getHeader("x-requested-with");
                    if ("XMLHttpRequest".equals(header)) {
                        response.setContentType("application/plain;charset=utf-8");
                        PrintWriter writer = response.getWriter();
                        writer.write(JSONUtils.getJSONString(404, "您没有权限访问该资源！！！"));
                    } else {
                        response.sendRedirect(request.getContextPath() + "/error/404");
                    }
                }
            });

        http.logout().logoutUrl("/securitylogout");

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/static/**", "/templates/**");
    }
}
