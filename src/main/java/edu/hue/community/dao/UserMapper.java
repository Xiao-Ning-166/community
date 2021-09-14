package edu.hue.community.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.hue.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 47552
 * @date 2021/09/12
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {


    /**
     * 通过id获取用户信息
     * @param id 主键
     * @return User 对象
     */
    User getUserById(Integer id);

    /**
     * 通过 id 修改密码
     * @param id
     * @param password 新密码
     * @return
     */
    int updatePasswordById(Integer id, String password);

    /**
     * 插入一个用户信息
     * @param user
     * @return
     */
    int insertUser(User user);

}
