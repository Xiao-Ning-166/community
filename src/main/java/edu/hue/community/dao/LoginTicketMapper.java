package edu.hue.community.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.hue.community.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 47552
 * @date 2021/09/15
 */
@Mapper
public interface LoginTicketMapper extends BaseMapper<LoginTicket> {
}
