package edu.hue.community;

import edu.hue.community.dao.DiscussPostMapper;
import edu.hue.community.dao.elasticsearch.DiscussPostRepository;
import edu.hue.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 * @author 47552
 * @date 2021/09/24
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticsearchTest {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Test
    public void testInsert() {
        discussPostRepository.save(discussPostMapper.selectById(109));
        discussPostRepository.save(discussPostMapper.selectById(110));
        discussPostRepository.save(discussPostMapper.selectById(111));
    }

    @Test
    public void testInsertList() {
        //discussPostRepository.saveAll(discussPostMapper.selectList(null));
        elasticsearchOperations.save(discussPostMapper.selectList(null));
    }

    @Test
    public void testDelete() {
        discussPostRepository.deleteAll();
    }

    @Test
    public void testUpdate() {
        DiscussPost post = discussPostMapper.selectById(111);
        post.setTitle("我是新人");
        post.setContent("hello elasticsearch");
        discussPostRepository.save(post);
    }

    @Test
    public void testSearch() {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬","title","content").type(MultiMatchQueryBuilder.Type.BEST_FIELDS))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                /*.withPageable(PageRequest.of(0,10))*/
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<span>").postTags("</span>"),
                        new HighlightBuilder.Field("content").preTags("<span>").postTags("</span>")
                ).build();
        SearchHits<DiscussPost> search = elasticsearchRestTemplate.search(query, DiscussPost.class);
        //long totalHits = search.getTotalHits();
        //System.out.println(totalHits);
        //List<SearchHit<DiscussPost>> searchHits = search.getSearchHits();
        //for (SearchHit<DiscussPost> searchHit : searchHits) {
        //    DiscussPost post = searchHit.getContent();
        //    System.out.println(post);
        //    Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
        //    System.out.println("高亮：" + highlightFields.get("title").toString());
        //    System.out.println("高亮：" + highlightFields.get("content").toString());
        //
        //}
        /*
        SearchHits<Product> search = elasticsearchRestTemplate.search(queryBuilder.build(), Product.class);
    for (SearchHit<Product> hit : search.getSearchHits()) {
        Product content = hit.getContent();
        List<String> name = hit.getHighlightField(fieId);
        System.out.println(name);
        StringBuilder stringBuilder = new StringBuilder();
        for (String text : name) {
            stringBuilder.append(text);
        }
        content.setName(stringBuilder.toString());
    }
    //当前页数据
    search.getSearchHits().forEach(System.out::println);
         */

        SearchPage<DiscussPost> page = SearchHitSupport.searchPageFor(search, PageRequest.of(0, 10));
        System.out.println("总页数：" + page.getTotalPages());
        System.out.println("当前页数：" + page.getNumber());
        System.out.println("当前页的记录数：" + page.getSize());
        System.out.println("总记录数：" + page.getTotalElements());
        SearchHits<DiscussPost> searchHits = page.getSearchHits();
        for (SearchHit<DiscussPost> searchHit : searchHits) {
            DiscussPost content = searchHit.getContent();
            List<String> titles = searchHit.getHighlightField("title");
            System.out.println("高亮之前：" + titles);
            StringBuilder stringBuilder = new StringBuilder();
            for (String title : titles) {
                stringBuilder.append(title);
            }
            content.setTitle(stringBuilder.toString());
            System.out.println("高亮：" + content);
        }
    }

    @Test
    public void testSearch02() {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬","title","content").type(MultiMatchQueryBuilder.Type.BEST_FIELDS))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0,10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        SearchHits<DiscussPost> search = elasticsearchOperations.search(query, DiscussPost.class);

        //List<SearchHit<DiscussPost>> searchHits = search.getSearchHits();
        //for (SearchHit<DiscussPost> searchHit : searchHits) {
        //    DiscussPost content = searchHit.getContent();
        //    System.out.println(content);
        //}

        SearchPage<DiscussPost> page = SearchHitSupport.searchPageFor(search, PageRequest.of(0, 10));
        System.out.println("总页数：" + page.getTotalPages());
        System.out.println("当前页数：" + page.getNumber());
        System.out.println("当前页的记录数：" + page.getSize());
        System.out.println("总记录数：" + page.getTotalElements());
        SearchHits<DiscussPost> searchHits = page.getSearchHits();
        for (SearchHit<DiscussPost> searchHit : searchHits) {
            DiscussPost content = searchHit.getContent();
            List<String> titles = searchHit.getHighlightField("title");
            System.out.println("高亮之前：" + titles);
            StringBuilder stringBuilder = new StringBuilder();
            for (String title : titles) {
                stringBuilder.append(title);
            }
            content.setTitle(stringBuilder.toString());
            System.out.println("高亮：" + content);
        }
    }

}
