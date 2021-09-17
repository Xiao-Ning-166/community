package edu.hue.community.controller;

import edu.hue.community.entity.User;
import edu.hue.community.service.UserService;
import edu.hue.community.util.MessageConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * @author 47552
 * @date 2021/09/15
 */
@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    /**
     * 去注册界面
     * @return
     */
    @GetMapping("/register")
    public String goToRegister() {
        return "/site/register";
    }

    /**
     * 注册账号
     * @param model
     * @param user 用户信息
     * @return
     */
    @PostMapping("/register")
    public String insertUser(Model model, User user) {
        Map<String, Object> map = userService.insertUser(user);
        if (map == null || map.isEmpty()) {
            // 说明注册成功
            model.addAttribute("msg","恭喜，您已经注册成功，我们向您的邮箱发送了一份激活邮件，激活之后即可使用我们的社区！！！");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }
        // 注册失败
        model.addAttribute("usernameMsg",map.get("usernameMsg"));
        model.addAttribute("passwordMsg",map.get("passwordMsg"));
        model.addAttribute("emailMsg",map.get("emailMsg"));
        return "/site/register";
    }

    /**
     * 激活账号
     * @param id 用户id
     * @param code 激活码
     * @return
     */
    @GetMapping("/activation/{id}/{code}")
    public String activeUser(Model model,
                             @PathVariable("id") Integer id,
                             @PathVariable("code") String code) {
        Integer activeStatus = userService.activeUser(id, code);
        if (MessageConstant.ACTIVATE_REUSE.equals(activeStatus)) {
            // 重复激活
            model.addAttribute("msg","该账号已激活，请勿重复激活！！！");
            model.addAttribute("target", "/login");
        } else if (MessageConstant.ACTIVATE_SUCCESS.equals(activeStatus)) {
            // 激活成功
            model.addAttribute("msg","您的账号已激活，可以正常使用了！！！");
            model.addAttribute("target", "/login");
        } else {
            // 激活失败
            model.addAttribute("msg","激活失败，请重试！！！");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }

}
