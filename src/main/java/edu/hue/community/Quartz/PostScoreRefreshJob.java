package edu.hue.community.Quartz;

import edu.hue.community.entity.DiscussPost;
import edu.hue.community.service.CommentService;
import edu.hue.community.service.DiscussPostService;
import edu.hue.community.service.ElasticsearchService;
import edu.hue.community.service.LikeService;
import edu.hue.community.util.MessageConstant;
import edu.hue.community.util.RedisUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 47552
 * @date 2021/10/01
 * 给帖子刷新分数的定时任务
 */
public class PostScoreRefreshJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private LikeService likeService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    private static Date startDate;
    static {
        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-10-1 00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String key = RedisUtils.getScoreKey();
        BoundSetOperations boundSetOperations = redisTemplate.boundSetOps(key);
        if (boundSetOperations.size() == 0) {
            logger.info("[任务取消] 没有需要更新分数的帖子");
            return;
        }

        logger.info("[任务开始] 正在刷新帖子的分数：" + boundSetOperations.size());

        while (boundSetOperations.size() > 0) {
            this.refresh((Integer) boundSetOperations.pop());
        }

        logger.info("[任务结束] 帖子分数刷新完成");
    }

    public void refresh(Integer postId) {
        DiscussPost post = discussPostService.getById(postId);
        if (post == null) {
            logger.info("该帖子不存在");
            return;
        }
        if (post.getStatus().equals(2)) {
            logger.info("该帖子已被删除");
            return;
        }
        // 是否加精
        boolean wonderful = post.getStatus().equals(1);
        // 点赞数
        Long likeCount = likeService.getLikeCount(MessageConstant.ENTITY_TYPE_POST, postId);
        // 评论数
        Integer commentCount = post.getCommentCount();
        // 权重
        double weight = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        // 计算分数
        Double score = Math.log10(Math.max(weight,1))
                + (post.getCreateTime().getTime() - startDate.getTime())/(1000 * 60 * 60 * 24);
        post.setScore(score);
        // 更新帖子分数
        discussPostService.updateById(post);
        // 更新 es 中的数据
        elasticsearchService.insetPost(post);
    }
}
