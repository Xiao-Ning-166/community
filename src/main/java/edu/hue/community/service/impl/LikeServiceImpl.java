package edu.hue.community.service.impl;

import edu.hue.community.service.LikeService;
import edu.hue.community.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @author 47552
 * @date 2021/09/20
 * 处理和点赞相关的业务
 */
@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 更新点赞次数，如果某用户没点赞则+1，点过则-1
     * @param userId     点赞用户的id
     * @param entityType 被点赞内容的类型（帖子/评论）
     * @param entityId   被点赞内容的id
     * @param entityUserId  实体作者的id
     * @return
     */
    @Override
    public void updateLikeCount(Integer userId, Integer entityType, Integer entityId, Integer entityUserId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityKey = RedisUtils.getRedisKey(entityType, entityId);
                String userKey = RedisUtils.getUserKey(entityUserId);

                // 判断当前用户是否为该评论点赞
                Boolean flag = redisTemplate.opsForSet().isMember(entityKey, userId);

                // 开启 redis 事务
                redisTemplate.multi();

                if (flag) {
                    // 点过赞。取消点赞、对方点赞数量-1
                    redisTemplate.opsForSet().remove(entityKey, userId);
                    redisTemplate.opsForValue().decrement(userKey);
                } else {
                    // 没有点过赞。点赞、对方点赞数量+1
                    redisTemplate.opsForSet().add(entityKey, userId);
                    redisTemplate.opsForValue().increment(userKey);
                }

                return redisTemplate.exec();
            }
        });
    }

    /**
     * 获取某实体的点赞数量
     * @param entityType 被点赞内容的类型（帖子/评论）
     * @param entityId   被点赞内容的id
     * @return
     */
    @Override
    public Long getLikeCount(Integer entityType, Integer entityId) {
        String entityKey = RedisUtils.getRedisKey(entityType, entityId);
        Long likeCount = redisTemplate.opsForSet().size(entityKey);
        return likeCount;
    }

    /**
     * 查询用户是否给实体点过赞
     * @param userId     点赞用户的id
     * @param entityType 被点赞内容的类型（帖子/评论）
     * @param entityId   被点赞内容的id
     * @return 1表示点过赞，0表示没点过赞
     */
    @Override
    public Integer getLikeStatus(Integer userId, Integer entityType, Integer entityId) {
        String entityKey = RedisUtils.getRedisKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityKey,userId) ? 1 : 0;
    }

    /**
     * 通过用户id查询该用户获得的赞的数量
     * @param userId
     * @return
     */
    @Override
    public Integer getLikeCountByUserId(Integer userId) {
        String userKey = RedisUtils.getUserKey(userId);
        Integer likeCount = (Integer) redisTemplate.opsForValue().get(userKey);
        return likeCount == null ? 0 : likeCount;
    }
}
