package edu.hue.community.controller;

import cn.hutool.core.util.StrUtil;
import edu.hue.community.annotation.LoginRequired;
import edu.hue.community.entity.DiscussPost;
import edu.hue.community.entity.User;
import edu.hue.community.service.DiscussPostService;
import edu.hue.community.util.HostHolder;
import edu.hue.community.util.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * @author 47552
 * @date 2021/09/17
 * 和帖子相关的操作
 */
@Controller
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 发布一个帖子
     * @param title 帖子的标题
     * @param content 帖子的内容
     * @return
     */
    @LoginRequired
    @PostMapping("/publishPost")
    @ResponseBody
    public String publishPost(String title, String content) {
        User user = hostHolder.getUser();
        Integer userId = user.getId();
        // 空值判断
        if (title == null || StrUtil.isBlankIfStr(title)) {
            return JSONUtils.getJSONString(601,"帖子标题不能为空！！！");
        }
        if (content == null || StrUtil.isBlankIfStr(content)) {
            return JSONUtils.getJSONString(602, "帖子内容不能为空！！！");
        }
        DiscussPost discussPost = new DiscussPost();
        // 设置帖子的相关信息
        discussPost.setUserId(userId);
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        // 保存帖子
        discussPostService.insertDiscussPost(discussPost);
        // 如果保存成功
        return JSONUtils.getJSONString(200,"帖子发布成功！！！");
    }

}
