package edu.hue.community.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.hue.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import javax.websocket.server.PathParam;

/**
 * @author 47552
 * @date 2021/09/12
 */
@Mapper
public interface DiscussPostMapper extends BaseMapper<DiscussPost> {


}
