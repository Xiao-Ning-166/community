package edu.hue.community;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.hue.community.entity.DiscussPost;
import edu.hue.community.service.DiscussPostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 47552
 * @date 2021/10/03
 */
@ContextConfiguration(classes = CommunityApplication.class)
@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheTest {

    @Autowired
    private DiscussPostService discussPostService;

    @Test
    public void testCache01() {
        Page<DiscussPost> page = new Page<>(0,10);
        System.out.println(discussPostService.pageQuery(page,null, 1));
        System.out.println(discussPostService.pageQuery(page,null, 1));
        System.out.println(discussPostService.pageQuery(page,null, 1));
        System.out.println(discussPostService.pageQuery(page,null, 0));
    }

}
