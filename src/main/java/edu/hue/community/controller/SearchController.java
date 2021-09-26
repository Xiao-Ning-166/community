package edu.hue.community.controller;

import edu.hue.community.entity.DiscussPost;
import edu.hue.community.entity.User;
import edu.hue.community.service.ElasticsearchService;
import edu.hue.community.service.LikeService;
import edu.hue.community.service.UserService;
import edu.hue.community.util.MessageConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
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
 * @date 2021/09/26
 */
@Controller
public class SearchController {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @GetMapping("/search")
    public String searchByKeyword(Model model, String keyword,
                                  @RequestParam(value = "current", defaultValue = "0") Integer current) {
        SearchPage<DiscussPost> page = elasticsearchService.search(keyword, current, 10);
        List<SearchHit<DiscussPost>> searchHits = page.getContent();
        List<Map<String, Object>> list = new ArrayList<>();
        if (searchHits != null) {
            for (SearchHit<DiscussPost> searchHit : searchHits) {
                Map<String, Object> map = new HashMap<>();
                DiscussPost post = searchHit.getContent();
                map.put("post", post);
                // 点赞数量
                Long likeCount = likeService.getLikeCount(MessageConstant.ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);
                // 作者信息
                User user = userService.getUserById(post.getUserId());
                map.put("user", user);
                list.add(map);
            }
        }
        model.addAttribute("list",list);
        model.addAttribute("keyword",keyword);
        model.addAttribute("page",page);

        return "/site/search";
    }

}
