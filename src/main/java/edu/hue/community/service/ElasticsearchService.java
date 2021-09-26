package edu.hue.community.service;

import edu.hue.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.core.SearchPage;

/**
 * @author 47552
 * @date 2021/09/26
 */
public interface ElasticsearchService {

    /**
     * 向 ES 中保存帖子
     * @param discussPost
     */
    void insetPost(DiscussPost discussPost);

    /**
     * 从 ES 中删除帖子
     * @param id
     */
    void deletePostById(Integer id);

    /**
     * 从 ES 中查找帖子
     * @param keyword
     * @return
     */
    SearchPage<DiscussPost> search(String keyword, Integer current, Integer size);
}
