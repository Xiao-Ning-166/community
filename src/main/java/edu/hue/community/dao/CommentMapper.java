package edu.hue.community.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.hue.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 47552
 * @date 2021/09/18
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
