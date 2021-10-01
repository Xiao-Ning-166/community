package edu.hue.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hue.community.dao.DiscussPostMapper;
import edu.hue.community.entity.DiscussPost;
import edu.hue.community.service.DiscussPostService;
import edu.hue.community.util.RedisUtils;
import edu.hue.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

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


    /**
     * 分页查询
     * @param page
     * @param userId
     * @return
     */
    @Override
    public Page pageQuery(Page page, Integer userId, Integer queryMode) {
        Page discussPostPage = null;
        // 封装查询条件
        if (queryMode.equals(0)) {
            // 按照时间查询
            QueryWrapper<DiscussPost> queryByTime = new QueryWrapper();
            queryByTime.ne("status", 2)
                       .eq(userId != null, "user_id", userId)
                       .orderByDesc("type", "create_time");
            // 分页查询
            discussPostPage  = this.page(page, queryByTime);
        } else {
            // 按照热度查询
            QueryWrapper<DiscussPost> queryByScore = new QueryWrapper<>();
            queryByScore.ne("status",2)
                        .eq(userId != null, "user_id", userId)
                        .orderByDesc("type", "score", "create_time");
            discussPostPage = this.page(page,queryByScore);
        }
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
