package edu.hue.community.controller;

import edu.hue.community.entity.User;
import edu.hue.community.service.LikeService;
import edu.hue.community.util.HostHolder;
import edu.hue.community.util.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 47552
 * @date 2021/09/20
 */
@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping("/like")
    @ResponseBody
    public String like(Integer entityType, Integer entityId, Integer entityUserId) {
        User user = hostHolder.getUser();
        // 更新点赞数量
        likeService.updateLikeCount(user.getId(),entityType,entityId,entityUserId);
        // 查询点赞数量
        Long likeCount = likeService.getLikeCount(entityType, entityId);
        // 查询当前用户是否点赞
        Integer likeStatus = likeService.getLikeStatus(user.getId(), entityType, entityId);

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        return JSONUtils.getJSONString(200,null,map);
    }

}
