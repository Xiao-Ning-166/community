package edu.hue.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.hue.community.entity.DiscussPost;
import edu.hue.community.entity.User;
import edu.hue.community.service.DiscussPostService;
import edu.hue.community.service.LikeService;
import edu.hue.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 47552
 * @date 2021/09/13
 */
@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    /**
     * 分页查询
     * @param model
     * @param current
     * @return
     */
    @GetMapping("/index")
    public String pageQuery(Model model,
                            @RequestParam(value = "current", required = false, defaultValue = "0") Integer current) {
        Page pageQuery = new Page(current,10);
        Page page = discussPostService.pageQuery(pageQuery,null);
        List<Map<String, Object>> list = new ArrayList<>();
        List<DiscussPost> records = pageQuery.getRecords();
        if (records != null) {
            for (DiscussPost record : records) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", record);
                User user = userService.getUserById(record.getUserId());
                map.put("user", user);
                // 查询帖子赞的数量
                Long likeCount = likeService.getLikeCount(1, record.getId());
                map.put("likeCount", likeCount);
                list.add(map);
            }
        }
        model.addAttribute("discussPosts",list);
        model.addAttribute("page",page);
        return "/index";
    }


    @GetMapping("/error")
    public String error() {
        return "/error/500";
    }

}
