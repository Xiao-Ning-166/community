package edu.hue.community.util;

import edu.hue.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author 47552
 * @date 2021/09/16
 * 用于持有用户信息，代替 session 对象
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
