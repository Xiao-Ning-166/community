package edu.hue.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hue.community.dao.CommentMapper;
import edu.hue.community.entity.Comment;
import edu.hue.community.entity.DiscussPost;
import edu.hue.community.service.CommentService;
import edu.hue.community.service.DiscussPostService;
import edu.hue.community.util.MessageConstant;
import edu.hue.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 47552
 * @date 2021/09/18
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    /**
     * 帖子评论信息的分页查询
     * @param page
     * @param entityType
     * @param entityId
     * @return
     */
    @Override
    public Page<Comment> queryForPage(Page page, Integer entityType, Integer entityId) {
        QueryWrapper<Comment> query = new QueryWrapper();
        Map<String, Object> map = new HashMap<>();
        map.put("entity_type", entityType);
        map.put("entity_id", entityId);
        query.allEq(map).orderByAsc("create_time");
        Page result = this.page(page, query);
        return result;
    }

    /**
     * 添加评论
     * @param comment 评论信息
     * @return
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED)
    @Override
    public void insertComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空！！！");
        }

        // 转义html标签
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        // 过滤敏感词
        comment.setContent(sensitiveFilter.filter(comment.getContent()));

        this.save(comment);

        // 更新评论数量
        if (comment.getEntityType().equals(MessageConstant.ENTITY_TYPE_POST)) {
            QueryWrapper<Comment> query = new QueryWrapper();
            query.eq("status",0)
                 .eq("entity_type",comment.getEntityType()).eq("entity_id",comment.getEntityId());
            int count = this.count(query);
            UpdateWrapper<DiscussPost> update = new UpdateWrapper();
            update.set("comment_count",count).eq("id",comment.getEntityId());
            discussPostService.update(update);
        }
    }


}
