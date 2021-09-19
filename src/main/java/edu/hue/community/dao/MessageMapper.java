package edu.hue.community.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.hue.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 47552
 * @date 2021/09/19
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 查询用户的所有会话和每个会话对应的最新的消息
     * @param page
     * @param userId
     * @return
     */
    IPage<Message> listConversation(IPage<Message> page, Integer userId);
}
