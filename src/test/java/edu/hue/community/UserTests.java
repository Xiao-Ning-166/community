package edu.hue.community;

import edu.hue.community.dao.UserMapper;
import edu.hue.community.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class UserTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testSelect() {

        User user01 = userMapper.getUserById(1);
        System.out.println(user01);

    }


}
