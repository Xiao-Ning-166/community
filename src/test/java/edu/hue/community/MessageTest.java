package edu.hue.community;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.hue.community.entity.DiscussPost;
import edu.hue.community.service.DiscussPostService;
import edu.hue.community.service.MessageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author 47552
 * @date 2021/09/12
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MessageTest {

    @Autowired
    private MessageService messageService;
    
    @Test
    public void testPage() {
        IPage page = new Page(0,20);
        IPage page1 = messageService.listConversation(page, 111);
        List records = page1.getRecords();
        for (Object record : records) {
            System.out.println(record);
        }
        System.out.println(page1.getTotal());
    }

    @Test
    public void testPage02() {
        IPage page = new Page(0,20);
        IPage page1 = messageService.listLetter(page, "111_112");
        List records = page1.getRecords();
        for (Object record : records) {
            System.out.println(record);
        }
        System.out.println(page.getTotal());
    }

    @Test
    public void testPage03() {
        Integer letterUnreadCount = messageService.getLetterUnreadCount(131, "111_131");
        System.out.println(letterUnreadCount);
    }
    
}