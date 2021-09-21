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
    Page pageQuery(Page page, Integer userId);

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
}
