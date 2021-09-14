package edu.hue.community;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.hue.community.entity.DiscussPost;
import edu.hue.community.service.DiscussPostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author 47552
 * @date 2021/09/12
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class DiscussPostTest {

    @Autowired
    private DiscussPostService discussPostService;
    
    @Test
    public void testPager() {
        Page page = new Page(0,10);
        QueryWrapper<DiscussPost> query = new QueryWrapper();
        Integer userId = 101;
        query.ne("status",1)
             .eq(userId != null,"user_id",userId)
             .orderByDesc("type","create_time");
        Page page1 = discussPostService.page(page, query);
        List records = page1.getRecords();
        records.forEach(record -> {
            System.out.println(record);
        });
    }
    
}