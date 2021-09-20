package edu.hue.community.controller;

import cn.hutool.core.util.StrUtil;
import edu.hue.community.annotation.LoginRequired;
import edu.hue.community.entity.User;
import edu.hue.community.service.LikeService;
import edu.hue.community.service.UserService;
import edu.hue.community.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.*;

/**
 * @author 47552
 * @date 2021/09/16
 * 与账号有关的操作
 */
@Controller
@Slf4j
public class UserController {

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    /**
     * 去往账号设置页面
     * @return
     */
    @LoginRequired
    @GetMapping("/setting")
    public String goToSetting() {
        return "/site/setting";
    }

    /**
     * 更新头像
     * @param headerImage
     * @param model
     * @return
     */
    @LoginRequired
    @PostMapping("/updateHeader")
    public String updateHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("msg","文件不能为空！！！");
            return "/site/setting";
        }
        // 获取文件后缀
        String filename = headerImage.getOriginalFilename();
        String postfix = filename.substring(filename.lastIndexOf("."));
        if (StrUtil.isBlankIfStr(postfix)) {
            model.addAttribute("msg","文件格式不正确！！！");
            return "/site/setting";
        }
        // 生成随机文件名
        filename = StrUtil.uuid() + postfix;
        File dest = new File(uploadPath + "/" + filename);
        try {
            // 上传文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            log.error("文件上传失败！！原因：" + e.getMessage());
            throw new RuntimeException("文件上传失败！！！", e);
        }
        User user = hostHolder.getUser();
        String headerUrl = domain + "/header/" + filename;
        // 更新用户头像
        user.setHeaderUrl(headerUrl);
        userService.updateById(user);

        return "redirect:/setting";
    }

    @GetMapping("/header/{filename}")
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {
        // 服务器存放路径
        filename = uploadPath + "/" + filename;
        // 文件后缀
        String postfix = filename.substring(filename.lastIndexOf(".") + 1);
        response.setContentType("image/" + postfix);
        FileInputStream fis = null;
        try {
            OutputStream out = response.getOutputStream();
            fis = new FileInputStream(filename);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            log.error("获取头像失败！！！原因：" + e.getMessage());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @LoginRequired
    @PostMapping("/updatePassword")
    public String updatePassword(String oldPassword, String newPassword, Model model) {
        User user = hostHolder.getUser();
        // 空值判断
        if (user == null) {
            model.addAttribute("oldPasswordMsg", "未登录，请先登录！！！");
            return "/site/setting";
        }
        if (oldPassword == null || StrUtil.isBlankIfStr(oldPassword)) {
            model.addAttribute("oldPasswordMsg","原始密码不能为空！！！");
            return "/site/setting";
        }
        if (newPassword == null || StrUtil.isBlankIfStr(newPassword)) {
            model.addAttribute("newPasswordMsg", "新密码不能为空！！！");
            return "/site/setting";
        }
        // 检查原密码是否正确
        if (!user.getPassword().equals(DigestUtils.md5DigestAsHex((oldPassword+user.getSalt()).getBytes()))) {
            model.addAttribute("oldPasswordMsg", "原始密码不正确！！！");
            return "/site/setting";
        }
        // 更新密码
        user.setPassword(DigestUtils.md5DigestAsHex((newPassword + user.getSalt()).getBytes()));
        userService.updateById(user);

        return "redirect:/logout";
    }

    /**
     * 去往个人主页
     * @param userId
     * @param model
     * @return
     */
    @GetMapping("/profile/{userId}")
    public String profile(@PathVariable("userId") Integer userId, Model model) {
        User user = userService.getById(userId);
        // 获取当前用户的被点赞数
        Integer likeCount = likeService.getLikeCountByUserId(userId);
        model.addAttribute("user",user);
        model.addAttribute("likeCount",likeCount);
        return "/site/profile";
    }
}
