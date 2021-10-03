package edu.hue.community.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import edu.hue.community.dao.DiscussPostMapper;
import edu.hue.community.entity.DiscussPost;
import edu.hue.community.service.DiscussPostService;
import edu.hue.community.util.RedisUtils;
import edu.hue.community.util.SensitiveFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 47552
 * @date 2021/09/13
 */
@Service
public class DiscussPostServiceImpl
        extends ServiceImpl<DiscussPostMapper, DiscussPost>
        implements DiscussPostService {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostServiceImpl.class);

    @Value("${caffeine.post.max-size}")
    private int maxSize;

    @Value("${caffeine.post.expire-seconds}")
    private long expireSeconds;

    // 分页信息缓存
    private LoadingCache<String, Page<DiscussPost>> postPageCache;


    @PostConstruct
    public void initCache() {
        // 初始化帖子缓存
        postPageCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, Page<DiscussPost>>() {
                    @Override
                    public @Nullable Page<DiscussPost> load(@NonNull String s) throws Exception {
                        if (StrUtil.isBlankIfStr(s)) {
                            throw new IllegalArgumentException("参数不正确！！！");
                        }
                        String[] params = s.split(":");
                        if (params == null || params.length != 2) {
                            throw new IllegalArgumentException("参数不正确！！！");
                        }
                        logger.debug("正在查询数据库");
                        int start = Integer.valueOf(params[0]);
                        int size = Integer.valueOf(params[1]);
                        Page<DiscussPost> page = new Page<>(start, size);
                        QueryWrapper<DiscussPost> query = new QueryWrapper<>();
                        query.ne("status",2)
                                .orderByDesc("type", "score", "create_time");
                        Page<DiscussPost> discussPostPage = discussPostMapper.selectPage(page, query);
                        return discussPostPage;
                    }
                });

    }

    /**
     * 分页查询
     * @param page
     * @param userId
     * @return
     */
    @Override
    public Page pageQuery(Page page, Integer userId, Integer queryMode) {
        if (queryMode.equals(1)) {
            String key = page.getCurrent() + ":" + page.getSize();
            return postPageCache.get(key);
        }

        logger.debug("正在查询数据库");
        // 封装查询条件
        QueryWrapper<DiscussPost> queryByTime = new QueryWrapper();
        queryByTime.ne("status", 2)
                   .eq(userId != null, "user_id", userId)
                   .orderByDesc("type")
                   .orderByDesc(queryMode.equals(1), "score")
                   .orderByDesc("create_time");
        // 分页查询
        Page discussPostPage  = this.page(page, queryByTime);

        return discussPostPage;
    }

    /**
     * 保存帖子
     * @param discussPost 帖子的信息
     * @return
     */
    @Override
    public boolean insertDiscussPost(DiscussPost discussPost) {
        if (discussPost == null) {
            throw new IllegalArgumentException("参数不能为空！！！");
        }
        // 转义HTML标签
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        // 过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return this.save(discussPost);
    }

    /**
     * 获取某个用户发过的所有帖子
     * @param page
     * @param userId
     * @return
     */
    @Override
    public IPage<DiscussPost> listPost(IPage<DiscussPost> page, Integer userId) {
        QueryWrapper<DiscussPost> query = new QueryWrapper();
        query.eq("user_id",userId).ne("status",2)
             .orderByDesc("create_time", "type");
        IPage<DiscussPost> discussPostPage = this.page(page, query);
        return discussPostPage;
    }


    /**
     * 根据帖子id修改帖子的状态
     * 0-正常; 1-精华; 2-拉黑;
     * @param id
     * @param status
     */
    @Override
    public void updatePostStatusById(Integer id, Integer status) {
        UpdateWrapper<DiscussPost> update = new UpdateWrapper();
        update.set("status",status).eq("id",id);
        this.update(update);
    }

    /**
     * 根据id修改帖子的类型
     * 0：普通 1：置顶
     * @param id
     * @param type
     */
    @Override
    public void updatePostTypeById(Integer id, Integer type) {
        UpdateWrapper<DiscussPost> update = new UpdateWrapper();
        update.set("type",type).eq("id",id);
        this.update(update);
    }
}
