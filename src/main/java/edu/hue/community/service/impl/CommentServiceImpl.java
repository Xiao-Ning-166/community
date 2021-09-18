package edu.hue.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hue.community.dao.CommentMapper;
import edu.hue.community.entity.Comment;
import edu.hue.community.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 47552
 * @date 2021/09/18
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {


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
}
