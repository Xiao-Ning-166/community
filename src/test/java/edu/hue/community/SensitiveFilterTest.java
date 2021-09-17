package edu.hue.community;

import edu.hue.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 47552
 * @date 2021/09/17
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveFilterTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String str01 = sensitiveFilter.filter("这里可以赌博、可以嫖娼、可以吸毒，哈哈！！！");
        System.out.println(str01);

        String str02 = sensitiveFilter.filter("这里可以的😊赌😊博😊、可以😊嫖😊娼😊、可以😊吸😊毒😊，哈哈！！！");
        System.out.println(str02);
    }

}
