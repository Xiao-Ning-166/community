package edu.hue.community.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.code.kaptcha.Producer;
import com.sun.org.apache.xpath.internal.operations.Mod;
import edu.hue.community.entity.User;
import edu.hue.community.service.UserService;
import edu.hue.community.util.MailUtils;
import edu.hue.community.util.MessageConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 47552
 * @date 2021/09/14
 */
@Controller
@Slf4j
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private MailUtils mailUtils;

    /**
     * 去注册界面
     * @return
     */
    @GetMapping("/login")
    public String goToLogin() {
        return "/site/login";
    }

    @GetMapping("/getVerificationCode")
    public void getVerificationCode(HttpServletResponse response,
                                    HttpSession session) {
        // 生成验证码
        String verificationCode = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(verificationCode);

        // 将验证码存放在session 域中
        session.setAttribute("verificationCode", verificationCode);

        // 将验证码输出给客户端
        response.setContentType("image/png");
        try {
            OutputStream out = response.getOutputStream();
            ImageIO.write(image,"png",out);
        } catch (IOException e) {
            log.error("验证码输出给客户端失败，原因：",e.getMessage());
        }
    }

    /**
     * 登录
     * @param model
     * @param session
     * @param response
     * @param username
     * @param password
     * @param verificationCode
     * @param rememberMe
     * @return
     */
    @PostMapping("/login")
    public String login(Model model, HttpSession session, HttpServletResponse response,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam("verificationCode") String verificationCode,
                        @RequestParam(value = "rememberMe", required = false, defaultValue="false") Boolean rememberMe
                        ) {
        String realVerificationCode1 = (String) session.getAttribute("verificationCode");
        // 检查验证码是否正确
        if (!realVerificationCode1.equalsIgnoreCase(verificationCode)) {
            model.addAttribute("verificationCodeMsg","验证码不正确！！！");
            return "/site/login";
        }

        // 检查用户名和密码
        Integer timeout = rememberMe ? MessageConstant.REMEMBER_ME_TIMEOUT : MessageConstant.DEFAULT_TIMEOUT;
        Map<String, Object> map = userService.login(username, password, timeout);
        if (map.containsKey("ticket")) {
            // 登录成功
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath("/");
            cookie.setMaxAge(timeout * 1000);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }

    /**
     * 退出登录
     * @param ticket
     * @return
     */
    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login";
    }

    /**
     * 去忘记密码页面
     * @return
     */
    @GetMapping("/forget")
    public String goToForget() {
        return "/site/forget";
    }

    /**
     * 发送重置密码的验证码
     * @param model
     * @param session
     * @param email 接收验证码的邮箱
     * @return
     */
    @PostMapping("/getRestCode")
    public String getRestCode(Model model, HttpSession session, String email) {

        Map<String, String> map = userService.getRestCode(email);
        // 回显错误信息
        if (!map.containsKey("resetCode")){
            model.addAttribute("eamilMsg", map.get("eamilMsg"));
            return "/site/forget";
        }

        // 保存重置验证码
        String resetCode = map.get("resetCode");
        session.setAttribute("restCode",resetCode);

        return "/site/forget";
    }
    /**
     * 重置密码
     * @param model
     * @param email 邮箱
     * @param password 新密码
     * @param resetCode 验证码
     * @return
     */
    @PostMapping("/forget")
    public String forget(Model model, HttpSession session, String email, String password, String resetCode) {
        // 验证验证码
        String realRestCode = (String) session.getAttribute("restCode");
        if (!realRestCode.equals(resetCode)) {
            model.addAttribute("email", email);
            model.addAttribute("restCodeMsg", "验证码不正确！！！");
            return "/site/forget";
        }

        Map<String, Object> map = userService.resetPassword(email, password);
        if (map == null || map.size() == 0) {
            // 修改密码成功
            model.addAttribute("msg","您的密码已经重置成功，即将跳转登录页面。");
            model.addAttribute("target","/login");
            return "/site/operate-result";
        }

        // 回显错误信息
        model.addAttribute("email", email);
        model.addAttribute("emailMsg",map.get("emailMsg"));
        model.addAttribute("passwordMsg",map.get("passwordMsg"));

        return "/site/forget";
    }
}
