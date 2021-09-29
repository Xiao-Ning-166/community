package edu.hue.community.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.hue.community.annotation.LoginRequired;
import edu.hue.community.entity.Comment;
import edu.hue.community.entity.DiscussPost;
import edu.hue.community.entity.Event;
import edu.hue.community.entity.User;
import edu.hue.community.event.EventProducer;
import edu.hue.community.service.CommentService;
import edu.hue.community.service.DiscussPostService;
import edu.hue.community.service.LikeService;
import edu.hue.community.service.UserService;
import edu.hue.community.util.HostHolder;
import edu.hue.community.util.JSONUtils;
import edu.hue.community.util.MessageConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author 47552
 * @date 2021/09/17
 * 和帖子相关的操作
 */
@Controller
public class DiscussPostController {

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

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

        // 触发发布帖子事件
        Event event = new Event()
                .setTopic(MessageConstant.TOPIC_PUBLISH)
                .setEntityType(MessageConstant.ENTITY_TYPE_POST)
                .setEntityUserId(user.getId())
                .setEntityId(discussPost.getId());
        eventProducer.fireEvent(event);

        // 如果保存成功
        return JSONUtils.getJSONString(200,"帖子发布成功！！！");
    }

    /**
     * 查看帖子详情
     * @param model
     * @param discussPostId
     * @param current
     * @return
     */
    @GetMapping("/getDiscussPost/{discussPostId}")
    public String getDiscussPost(Model model,
                                 @PathVariable("discussPostId") Integer discussPostId,
                                 @RequestParam(value = "current", required = false, defaultValue = "0") Integer current) {
        if (discussPostId == null) {
            return "/index";
        }
        DiscussPost discussPost = discussPostService.getById(discussPostId);
        // 空值判断
        if (discussPost == null) {
            return "/index";
        }
        User user = userService.getUserById(discussPost.getUserId());
        // 查询帖子的评论数量
        Long likeCount = likeService.getLikeCount(1, discussPostId);
        model.addAttribute("likeCount",likeCount);
        // 当前用户是否为该帖子点赞
        Integer likeStatus = likeService.getLikeStatus(user.getId(), MessageConstant.ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeStatus",likeStatus);
        model.addAttribute("post",discussPost);
        model.addAttribute("user",user);

        // 帖子回复信息显示功能，稍后再写
        Page<Comment> page = new Page<>(current,5);
        // 给帖子的评论的分页查询
        Page<Comment> commentPage = commentService.queryForPage(page,
                MessageConstant.ENTITY_TYPE_POST, discussPost.getId());
        // 给帖子的评论
        List<Comment> records = commentPage.getRecords();
        // 存放给帖子评论的信息
        List<Map<String, Object>> commentList = new ArrayList<>();
        if (records != null) {
            for (Comment record : records) {
                Map<String, Object> map = new HashMap<>();
                // 评论的信息
                map.put("comment", record);
                // 评论人
                map.put("user",userService.getUserById(record.getUserId()));
                // 查询评论的点赞数量
                likeCount = likeService.getLikeCount(2, record.getId());
                map.put("commentLikeCount",likeCount);
                // 查看当前用户是否为该评论点赞
                likeStatus = likeService.getLikeStatus(user.getId(), MessageConstant.ENTITY_TYPE_COMMENT,record.getId());
                map.put("commentLikeStatus",likeStatus);
                // 查询评论的评论
                Page<Comment> page02 = new Page<>(0, Integer.MAX_VALUE);
                Page<Comment> pageForComment = commentService.queryForPage(page02,
                        MessageConstant.ENTITY_TYPE_COMMENT, record.getId());
                List<Comment> replyList = pageForComment.getRecords();
                // 存放给评论评论的信息
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyMap = new HashMap<>();
                        replyMap.put("reply", reply);
                        replyMap.put("user", userService.getUserById(reply.getUserId()));
                        // 回复目标
                        User target = reply.getTargetId() == 0?null:userService.getUserById(reply.getTargetId());
                        replyMap.put("target", target);
                        // 查询回复的评论数量
                        likeCount = likeService.getLikeCount(2, reply.getId());
                        replyMap.put("replyLikeCount",likeCount);
                        // 查看当前用户是否为该评论点赞
                        likeStatus = likeService.getLikeStatus(user.getId(), MessageConstant.ENTITY_TYPE_COMMENT,record.getId());
                        replyMap.put("replyLikeStatus",likeStatus);
                        replyVoList.add(replyMap);
                    }
                }
                // 回复列表
                map.put("replyMap", replyVoList);
                // 回复数量
                map.put("replyCount", page02.getTotal());
                //
                commentList.add(map);
            }
        }

        model.addAttribute("comments",commentList);
        model.addAttribute("page",commentPage);
        return "/site/discuss-detail";
    }

    /**
     * 展示某用户所有发过的帖子
     * 该功能需要登录
     * @param model
     * @param current
     * @return
     */
    @GetMapping("/myPost/{userId}")
    public String listPost(Model model,
                          @RequestParam(value = "current", required = false, defaultValue = "0") Integer current) {
        IPage<DiscussPost> page = new Page<>(current,10);
        IPage<DiscussPost> discussPostPage = discussPostService.listPost(page, hostHolder.getUser().getId());
        List<DiscussPost> postList = discussPostPage.getRecords();
        List<Map<String, Object>> postVoList = new ArrayList<>();
        for (DiscussPost discussPost : postList) {
            Map map = new HashMap();
            map.put("post",discussPost);
            Long likeCount = likeService.getLikeCount(MessageConstant.ENTITY_TYPE_POST, discussPost.getId());
            map.put("likeCount",likeCount);
            postVoList.add(map);
        }
        model.addAttribute("postVoList",postVoList);
        model.addAttribute("user",hostHolder.getUser());
        model.addAttribute("postCount",postVoList==null?0:postVoList.size());
        model.addAttribute("page",discussPostPage);
        return "/site/my-post";
    }

    /**
     * 设置置顶
     * @param discussPostId
     * @return
     */
    @GetMapping("/top/{discussPostId}")
    @ResponseBody
    public String setTop(@PathVariable("discussPostId") Integer discussPostId) {
        discussPostService.updatePostTypeById(discussPostId,MessageConstant.TYPE_TOP);

        // 触发发布帖子事件，修改es中数据属性
        Event event = new Event()
                .setTopic(MessageConstant.TOPIC_PUBLISH)
                .setEntityType(MessageConstant.ENTITY_TYPE_POST)
                .setEntityUserId(hostHolder.getUser().getId())
                .setEntityId(discussPostId);
        eventProducer.fireEvent(event);

        return JSONUtils.getJSONString(200,"帖子置顶成功！！！");
    }

    @GetMapping("/wonderful/{discussPostId}")
    @ResponseBody
    public String addWonderful(@PathVariable("discussPostId") Integer discussPostId) {
        discussPostService.updatePostStatusById(discussPostId, MessageConstant.STATUS_WONDERFUL);
        // 触发帖子发布事件
        Event event = new Event()
                .setTopic(MessageConstant.TOPIC_PUBLISH)
                .setEntityType(MessageConstant.ENTITY_TYPE_POST)
                .setEntityUserId(hostHolder.getUser().getId())
                .setEntityId(discussPostId);
        eventProducer.fireEvent(event);
        return JSONUtils.getJSONString(200, "帖子加精成功！！！");
    }

    @GetMapping("/deletePost/{discussPostId}")
    @ResponseBody
    public String deletePostById(@PathVariable("discussPostId") Integer discussPostId) {
        discussPostService.updatePostStatusById(discussPostId, MessageConstant.STATUS_DELETE);

        // 触发删帖事件
        Event event = new Event()
                .setTopic(MessageConstant.TOPIC_DELETE)
                .setEntityType(MessageConstant.ENTITY_TYPE_POST)
                .setEntityUserId(hostHolder.getUser().getId())
                .setEntityId(discussPostId);
        eventProducer.fireEvent(event);

        return JSONUtils.getJSONString(200,"删帖成功！！！");
    }

}
