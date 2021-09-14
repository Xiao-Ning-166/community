package edu.hue.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hue.community.dao.DiscussPostMapper;
import edu.hue.community.entity.DiscussPost;
import edu.hue.community.service.DiscussPostService;
import org.springframework.stereotype.Service;

/**
 * @author 47552
 * @date 2021/09/13
 */
@Service
public class DiscussPostServiceImpl
        extends ServiceImpl<DiscussPostMapper, DiscussPost>
        implements DiscussPostService {

    /**
     * 分页查询
     * @param page
     * @param userId
     * @return
     */
    @Override
    public Page pageQuery(Page page, Integer userId) {
        // 封装查询条件
        QueryWrapper<DiscussPost> query = new QueryWrapper();
        query.ne("status",1)
                .eq(userId != null,"user_id",userId)
                .orderByDesc("type","create_time");
        // 分页查询
        Page discussPostPage = this.page(page, query);
        return discussPostPage;
    }
}
