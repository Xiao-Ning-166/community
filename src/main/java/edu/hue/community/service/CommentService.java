package edu.hue.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.hue.community.entity.Comment;

/**
 * @author 47552
 * @date 2021/09/18
 */
public interface CommentService extends IService<Comment> {

    Page<Comment> queryForPage(Page page, Integer entityType, Integer entityId);

    /**
     * 添加评论
     * @param comment 评论信息
     * @return
     */
    void insertComment(Comment comment);

    /**
     * 查询某用户发过的所有帖子
     * @param page
     * @param userId
     * @return
     */
    IPage<Comment> listComment(IPage<Comment> page, Integer userId);

}
