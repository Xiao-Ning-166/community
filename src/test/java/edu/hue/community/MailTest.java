package edu.hue.community;

import edu.hue.community.util.MailUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author 47552
 * @date 2021/09/14
 * 测试发送邮件
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {

    @Autowired
    private MailUtils mailUtils;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * 发送文字
     * @throws Exception
     */
    @Test
    public void testMail() throws Exception {

        mailUtils.sendMail("475529787@qq.com","测试","测试发送邮件");
    }

    @Test
    public void testMailWithHTML() throws Exception {
        Context context = new Context();
        context.setVariable("username","张三");
        String message = templateEngine.process("/mail.html", context);
        System.out.println(message);
        mailUtils.sendMail("475529787@qq.com","测试发送HTML", message);
    }

}
