package edu.hue.community.service.impl;

import cn.hutool.Hutool;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hue.community.dao.LoginTicketMapper;
import edu.hue.community.dao.UserMapper;
import edu.hue.community.entity.LoginTicket;
import edu.hue.community.entity.User;
import edu.hue.community.service.UserService;
import edu.hue.community.util.MailUtils;
import edu.hue.community.util.MessageConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaoning
 * @date 2021/09/12
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailUtils mailUtils;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    public static final String DOMAIN_NAME = "http://localhost:8080";

    /**
     * 注册用户
     * @param user
     * @return
     */
    @Override
    public Map<String, Object> insertUser(User user) {
        Map<String, Object> map = new HashMap<>();

        // 判断是否有空值
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StrUtil.isBlankIfStr(user.getUsername())) {
            map.put("usernameMsg", "用户名不能为空！！！");
        }
        if (StrUtil.isBlankIfStr(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空！！！");
        }
        if (StrUtil.isBlankIfStr(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空！！！");
        }

        // 判断是否有相同用户名
        QueryWrapper queryByName = new QueryWrapper();
        queryByName.eq("username",user.getUsername());
        User one = userMapper.selectOne(queryByName);
        if (one != null) {
            map.put("usernameMsg", "用户名已被使用！！！");
            return map;
        }
        // 判断邮箱是否重复
        QueryWrapper queryByEmail = new QueryWrapper();
        queryByEmail.eq("email", user.getEmail());
        one = userMapper.selectOne(queryByEmail);
        if (one != null) {
            map.put("emailMsg", "邮箱已被注册！！！");
            return map;
        }

        // 设置字段，保存用户
        user.setSalt(StrUtil.uuid().substring(0,5));
        user.setPassword(DigestUtils.md5DigestAsHex((user.getPassword() + user.getSalt()).getBytes()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(StrUtil.uuid());
        user.setCreateTime(new Date());
        userMapper.insert(user);

        // 发送激活邮件
        Context context = new Context();
        context.setVariable("username",user.getUsername());
        String url = DOMAIN_NAME + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("activeUrl",url);
        String message = templateEngine.process("/mail/activation.html", context);
        mailUtils.sendMail(user.getEmail(),"激活账号",message);

        return map;
    }

    /**
     * 激活用户
     * @param id 用户id
     * @param code 激活码
     * @return
     */
    @Override
    public Integer activeUser(Integer id, String code) {
        User user = userMapper.selectById(id);
        if (user.getStatus() == 1) {
            // 重复激活
            return MessageConstant.ACTIVATE_REUSE;
        } else if (user.getActivationCode().equals(code)){
            // 激活成功
            user.setStatus(1);
            userMapper.updateById(user);
            return MessageConstant.ACTIVATE_SUCCESS;
        } else {
            // 激活失败
            return MessageConstant.ACTIVATE_FAIL;
        }
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @param timeout 凭证的超时时间（单位：ms）
     * @return
     */
    @Override
    public Map<String, Object> login(String username, String password, Integer timeout) {
        Map<String, Object> map = new HashMap<>();;

        // 空值判断
        if (StrUtil.isBlankIfStr(username)) {
            map.put("usernameMsg", "用户名不能为空！！！");
            return map;
        }
        if (StrUtil.isBlankIfStr(password)) {
            map.put("passwordMsg","密码不能为空！！！");
            return map;
        }

        // 验证账号信息
        QueryWrapper query = new QueryWrapper();
        query.eq("username",username);
        User user = userMapper.selectOne(query);
        if (user == null) {
            map.put("usernameMsg", "用户名错误！！！");
            return map;
        }
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该用户未激活，不能使用！！！");
            return map;
        }
        password = DigestUtils.md5DigestAsHex((password+user.getSalt()).getBytes());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码错误！！！");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(StrUtil.uuid());
        loginTicket.setStatus(0);
        // 设置过期时间默认 10 分钟
        loginTicket.setExpired(new Date(System.currentTimeMillis() + timeout * 1000));

        loginTicketMapper.insert(loginTicket);

        map.put("ticket", loginTicket.getTicket());

        return map;
    }

    /**
     * 退出登录
     * @param ticket 登录凭证
     */
    @Override
    public void logout(String ticket) {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket(ticket);
        loginTicket.setStatus(1);
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("ticket",ticket);
        loginTicketMapper.update(loginTicket,updateWrapper);
    }

    /**
     * 发送重置密码的验证码
     * @param email 接收验证码的邮箱
     * @return
     */
    @Override
    public Map<String, String> getRestCode(String email) {
        Map<String, String> map = new HashMap<>();

        // 空值判断
        if (email == null || StrUtil.isBlankIfStr(email)) {
            map.put("emailMsg", "邮箱不能为空！！！");
            return map;
        }

        // 判断邮箱是否存在
        QueryWrapper query = new QueryWrapper();
        query.eq("email", email);
        User user = userMapper.selectOne(query);
        if (user == null) {
            map.put("emailMsg", "该邮箱尚未被注册，无法用于找回密码！！！");
            return map;
        }

        // 生成验证码，发送邮件
        String resetCode = StrUtil.uuid().substring(0,6);
        Context context = new Context();
        context.setVariable("email",email);
        context.setVariable("resetCode",resetCode);
        String message = templateEngine.process("/mail/forget.html", context);
        mailUtils.sendMail(user.getEmail(),"找回密码",message);
        map.put("resetCode",resetCode);
        return map;
    }

    /**
     * 修改密码
     * @param email    邮箱
     * @param password 新密码
     * @return
     */
    @Override
    public Map<String, Object> resetPassword(String email, String password) {

        Map<String, Object> map = new HashMap<>();

        // 判断是否为空
        if (email == null || StrUtil.isBlankIfStr(email)) {
            map.put("emailMsg", "邮箱不能为空！！！");
            return map;
        }
        if (password == null || StrUtil.isBlankIfStr(password)) {
            map.put("passwordMsg", "密码不能为空！！！");
            return map;
        }

        // 验证邮箱
        QueryWrapper query = new QueryWrapper();
        query.eq("email",email);
        User user = userMapper.selectOne(query);
        if (user == null) {
            map.put("emailMsg", "该邮箱未被注册，无法找回密码！！！");
            return map;
        }
        user.setPassword(DigestUtils.md5DigestAsHex((password+user.getSalt()).getBytes()));

        return map;
    }


}
