package edu.hue.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hue.community.entity.User;

import java.util.Map;

/**
 * @author 47552
 * @date 2021/09/12
 */
public interface UserService extends IService<User> {

    /**
     * 注册用户
     * @param user
     * @return
     */
    Map<String, Object> insertUser(User user);

    /**
     * 激活用户
     * @param id
     * @param code
     * @return
     */
    Integer activeUser(Integer id, String code);

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @param timeout 登录凭证超时时间
     * @return
     */
    Map<String, Object> login(String username, String password, Integer timeout);

    /**
     * 退出登录
     * @param ticket
     */
    void logout(String ticket);

    /**
     * 发送验证码
     * @param email 接收验证码的邮箱
     * @return
     */
    Map<String, String> getRestCode(String email);

    /**
     * 修改密码
     * @param email 邮箱
     * @param password 新密码
     * @return
     */
    Map<String, Object> resetPassword(String email, String password);
}
