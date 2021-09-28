package edu.hue.community.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.hue.community.entity.Comment;
import edu.hue.community.entity.DiscussPost;
import edu.hue.community.entity.Event;
import edu.hue.community.event.EventProducer;
import edu.hue.community.service.CommentService;
import edu.hue.community.service.DiscussPostService;
import edu.hue.community.service.LikeService;
import edu.hue.community.service.UserService;
import edu.hue.community.util.HostHolder;
import edu.hue.community.util.MessageConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

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

    @Autowired
    private LikeService likeService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private EventProducer eventProducer;


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

        // 添加评论后。触发评论事件。给被评论的用户发送系统通知
        Event event = new Event();
        event.setTopic(MessageConstant.TOPIC_COMMENT)
             .setUserId(hostHolder.getUser().getId())
             .setEntityType(comment.getEntityType())
             .setEntityId(comment.getEntityId())
             .setData("postId", discussPostId);
        if (comment.getEntityType().equals(MessageConstant.ENTITY_TYPE_POST)) {
            DiscussPost target = discussPostService.getById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType().equals(MessageConstant.ENTITY_TYPE_COMMENT)) {
            Comment target = commentService.getById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        // 发送通知
        eventProducer.fireEvent(event);

        // 触发发布帖子事件
        event = new Event()
                .setTopic(MessageConstant.TOPIC_PUBLISH)
                .setEntityType(MessageConstant.ENTITY_TYPE_POST)
                .setEntityUserId(hostHolder.getUser().getId())
                .setEntityId(discussPostId);
        eventProducer.fireEvent(event);

        // 重定向显示评论详情
        return "redirect:/getDiscussPost/" + discussPostId;
    }

    /**
     * 查看我的回复
     * @param model
     * @param userId
     * @param current
     * @return
     */
    @GetMapping("/myReply/{userId}")
    public String listReply(Model model,
                            @PathVariable("userId") Integer userId,
                            @RequestParam(value = "current", required = false, defaultValue = "0") Integer current) {
        IPage<Comment> page = new Page<>(current,10);
        IPage<Comment> commentPage = commentService.listComment(page, userId);
        List<Comment> commentList = commentPage.getRecords();
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                Map<String, Object> map = new HashMap<>();
                map.put("reply", comment);
                Long likeCount = likeService.getLikeCount(MessageConstant.ENTITY_TYPE_COMMENT, comment.getId());
                map.put("likeCount",likeCount);
                Integer postId = getPostId(comment);
                DiscussPost post = discussPostService.getById(postId);
                map.put("post",post);
                commentVoList.add(map);
            }
        }
        model.addAttribute("user",hostHolder.getUser());
        model.addAttribute("commentVoList",commentVoList);
        model.addAttribute("commentCount",commentList==null?0:commentList.size());
        model.addAttribute("page",commentPage);

        return "/site/my-reply";
    }

    /**
     * 获取评论做所在的帖子的id
     * @return
     */
    private Integer getPostId(Comment comment) {
        Comment temp = comment;
        while (true) {
            if (temp.getEntityType().equals(MessageConstant.ENTITY_TYPE_POST)) {
                return temp.getEntityId();
            }
            temp = commentService.getById(temp.getEntityId());
        }
    }
}
