package edu.hue.community;

import cn.hutool.crypto.digest.DigestUtil;
import edu.hue.community.dao.UserMapper;
import edu.hue.community.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.DigestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class UserTests {


    @Test
    void testSelect() {
        String password = "123456789" + "60f13";
        String str1 = DigestUtils.md5DigestAsHex(password.getBytes()).toString();
        String str2 = DigestUtils.md5DigestAsHex(password.getBytes()).toString();
        System.out.println(str1);
        System.out.println(str2);
    }


}
