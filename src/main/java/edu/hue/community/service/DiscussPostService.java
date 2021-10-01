package edu.hue.community.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.hue.community.entity.DiscussPost;

/**
 * @author 47552
 * @date 2021/09/12
 */
public interface DiscussPostService extends IService<DiscussPost> {

    /**
     * 分页查询
     * @param page
     * @param userId
     * @return
     */
    Page pageQuery(Page page, Integer userId, Integer queryMode);

    /**
     * 保存帖子
     * @param discussPost 帖子的信息
     * @return
     */
    boolean insertDiscussPost(DiscussPost discussPost);

    /**
     * 获取某个用户发过的所有帖子
     * @param page
     * @param userId
     * @return
     */
    IPage<DiscussPost> listPost(IPage<DiscussPost> page, Integer userId);

    /**
     * 根据帖子id修改帖子的状态
     * 0-正常; 1-精华; 2-拉黑;
     * @param id
     * @param status
     */
    void updatePostStatusById(Integer id, Integer status);

    /**
     * 根据id修改帖子的类型
     * 0：普通 1：置顶
     * @param id
     * @param type
     */
    void updatePostTypeById(Integer id, Integer type);

}
