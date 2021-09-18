package edu.hue.community.controller;

import edu.hue.community.entity.Comment;
import edu.hue.community.service.CommentService;
import edu.hue.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;

/**
 * @author 47552
 * @date 2021/09/18
 */
@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 添加评论
     * @param discussPostId 被评论的帖子的id
     * @param comment 评论信息
     * @return
     */
    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") Integer discussPostId, Comment comment) {
        comment.setStatus(0);
        comment.setUserId(hostHolder.getUser().getId());
        comment.setCreateTime(new Date());
        commentService.insertComment(comment);
        // 重定向显示评论详情
        return "redirect:/getDiscussPost/" + discussPostId;
    }

}
