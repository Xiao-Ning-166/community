package edu.hue.community.service.impl;

import edu.hue.community.dao.elasticsearch.DiscussPostRepository;
import edu.hue.community.entity.DiscussPost;
import edu.hue.community.service.ElasticsearchService;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 47552
 * @date 2021/09/26
 */
@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {

    //@Autowired
    //private ElasticsearchRestTemplate elasticsearchRestTemplate;

    //@Autowired
    //private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private  ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private DiscussPostRepository discussPostRepository;

    /**
     * 向 ES 中保存帖子
     * @param discussPost
     */
    @Override
    public void insetPost(DiscussPost discussPost) {
        discussPostRepository.save(discussPost);
    }

    /**
     * 从 ES 中删除帖子
     * @param id
     */
    @Override
    public void deletePostById(Integer id) {
        discussPostRepository.deleteById(id);
    }

    /**
     * 从 ES 中查找帖子
     * @param keyword
     * @return
     */
    @Override
    public SearchPage<DiscussPost> search(String keyword, Integer current, Integer size) {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword,"title","content").type(MultiMatchQueryBuilder.Type.BEST_FIELDS))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current, size))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        SearchHits<DiscussPost> search = elasticsearchOperations.search(query, DiscussPost.class);
        SearchPage<DiscussPost> page = SearchHitSupport.searchPageFor(search, PageRequest.of(current, size));

        SearchHits<DiscussPost> searchHits = page.getSearchHits();
        for (SearchHit<DiscussPost> searchHit : searchHits) {
            DiscussPost post = searchHit.getContent();
            // 设置标题
            List<String> titles = searchHit.getHighlightField("title");
            StringBuilder stringBuilder = new StringBuilder();
            if (titles.size() > 0) {
                for (String title : titles) {
                    stringBuilder.append(title);
                }
                post.setTitle(stringBuilder.toString());
            }
            // 设置内容
            List<String> contents = searchHit.getHighlightField("content");
            StringBuilder content = new StringBuilder();
            if (contents.size() > 0) {
                for (String s : contents) {
                    content.append(s);
                }
                post.setContent(content.toString());
            }
        }

        return page;
    }
}
