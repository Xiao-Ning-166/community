package edu.hue.community.controller;

import edu.hue.community.entity.Event;
import edu.hue.community.entity.User;
import edu.hue.community.event.EventProducer;
import edu.hue.community.service.DiscussPostService;
import edu.hue.community.service.FollowService;
import edu.hue.community.service.UserService;
import edu.hue.community.util.HostHolder;
import edu.hue.community.util.JSONUtils;
import edu.hue.community.util.MessageConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author 47552
 * @date 2021/09/20
 */
@Controller
public class FollowController {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    /**
     * 关注
     * @param entityType
     * @param entityId
     * @return
     */
    @PostMapping("/follow")
    @ResponseBody
    public String follow(Integer entityType, Integer entityId) {
        User user = hostHolder.getUser();
        followService.setFollow(user.getId(),entityType,entityId);

        // 触发关注事件
        Event event = new Event();
        event.setTopic(MessageConstant.TOPIC_FOLLOW)
             .setUserId(user.getId())
             .setEntityType(entityType)
             .setEntityUserId(entityId);
        // 发送系统通知
        eventProducer.fireEvent(event);

        return JSONUtils.getJSONString(200,"已关注！！！");
    }

    /**
     * 取关
     * @param entityType
     * @param entityId
     * @return
     */
    @PostMapping("/unFollow")
    @ResponseBody
    public String unFollow(Integer entityType, Integer entityId) {
        User user = hostHolder.getUser();
        followService.removeFollow(user.getId(),entityType,entityId);
        return JSONUtils.getJSONString(200,"已取消关注！！！");
    }

    /**
     * 查看关注列表
     * @param userId
     * @param model
     * @param offset
     * @return
     */
    @GetMapping("/listFollowee/{userId}")
    public String listFollowee(@PathVariable("userId") Integer userId, Model model,
                               @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset) {
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        Long followeeCount = followService.getFolloweeCount(userId, MessageConstant.ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        List<Map<String, Object>> list = followService.listFollowee(userId,
                MessageConstant.ENTITY_TYPE_USER, offset * 5, 5);
        if (list != null) {
            for (Map<String, Object> map : list) {
                User targetUser = (User) map.get("user");
                Boolean followeeStatus = getFolloweeStatus(hostHolder.getUser().getId(),MessageConstant.ENTITY_TYPE_USER,targetUser.getId());
                map.put("followeeStatus", followeeStatus);
            }
        }
        model.addAttribute("list",list);
        model.addAttribute("offset",offset);
        return "/site/followee";
    }

    /**
     * 产看粉丝列表
     * @param userId
     * @param model
     * @param offset
     * @return
     */
    @GetMapping("/listFollower/{userId}")
    public String listFollower(@PathVariable("userId") Integer userId, Model model,
                               @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset) {
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);

        Long followeeCount = followService.getFollowerCount(MessageConstant.ENTITY_TYPE_USER,userId);
        model.addAttribute("followeeCount",followeeCount);

        List<Map<String, Object>> list = followService.listFollower(MessageConstant.ENTITY_TYPE_USER,userId, offset, 5);
        if (list != null) {
            for (Map<String, Object> map : list) {
                User targetUser = (User) map.get("user");
                Boolean followeeStatus = getFolloweeStatus(hostHolder.getUser().getId(),MessageConstant.ENTITY_TYPE_USER,targetUser.getId());
                map.put("followeeStatus", followeeStatus);
            }
        }
        model.addAttribute("list",list);
        model.addAttribute("offset",offset);
        return "/site/follower";
    }

    private Boolean getFolloweeStatus(Integer userId, Integer entityType, Integer entityId) {
        if (hostHolder.getUser() == null) {
            return false;
        }
        return followService.getFolloweeStatus(userId, entityType, entityId);
    }
}
