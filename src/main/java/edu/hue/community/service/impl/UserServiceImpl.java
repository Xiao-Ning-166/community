package edu.hue.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hue.community.dao.UserMapper;
import edu.hue.community.entity.User;
import edu.hue.community.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author xiaoning
 * @date 2021/09/12
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
