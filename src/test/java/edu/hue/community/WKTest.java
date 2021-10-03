package edu.hue.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * @author 47552
 * @date 2021/10/01
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class WKTest {

    @Test
    public void test01() {
        String cmd01 = "D:/OpenSource/wkhtmltopdf/bin/wkhtmltoimage --quality 75 https://www.qq.com/ F:/project/community/data/wk-img/qq.png";
        try {
            Runtime.getRuntime().exec(cmd01);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
