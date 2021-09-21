package edu.hue.community.service.impl;

import edu.hue.community.entity.User;
import edu.hue.community.service.FollowService;
import edu.hue.community.service.UserService;
import edu.hue.community.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author 47552
 * @date 2021/09/20
 * 处理关注、取消关注相关的业务
 */
@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    /**
     * 设置关注信息
     * @param userId
     * @param entityType 关注的实体类型
     * @param entityId   关注的实体id
     */
    @Override
    public void setFollow(Integer userId, Integer entityType, Integer entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 存放关注实体的Key
                String followeeKey = RedisUtils.getFolloweeKey(userId, entityType);
                // 关注自己的key
                String followerKey = RedisUtils.getFollowerKey(entityType, entityId);
                // 开启事务
                operations.multi();
                // 设置用户关注的实体集合
                operations.opsForZSet().add(followeeKey, entityId,System.currentTimeMillis());
                // 设置实体的粉丝的集合
                operations.opsForZSet().add(followerKey, userId,System.currentTimeMillis());
                return operations.exec();
            }
        });
    }

    /**
     * 取消关注
     * @param userId
     * @param entityType
     * @param entityId
     */
    @Override
    public void removeFollow(Integer userId, Integer entityType, Integer entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 存放关注实体的Key
                String followeeKey = RedisUtils.getFolloweeKey(userId, entityType);
                // 关注自己的key
                String followerKey = RedisUtils.getFollowerKey(entityType, entityId);
                // 开启事务
                operations.multi();
                // 从用户关注的实体集合中移除实体
                operations.opsForZSet().remove(followeeKey, entityId);
                // 从实体的粉丝的集合移除粉丝
                operations.opsForZSet().remove(followerKey, userId);
                return operations.exec();
            }
        });
    }

    /**
     * 查询粉丝的数量
     * @param entityType
     * @param entityId
     * @return
     */
    @Override
    public Long getFollowerCount(Integer entityType, Integer entityId) {
        String followerKey = RedisUtils.getFollowerKey(entityType, entityId);
        Long followerCount = redisTemplate.opsForZSet().zCard(followerKey);
        return followerCount;
    }

    /**
     * 查询关注者的数量
     * @param userId
     * @param entityType
     * @return
     */
    @Override
    public Long getFolloweeCount(Integer userId, Integer entityType) {
        String followeeKey = RedisUtils.getFolloweeKey(userId, entityType);
        Long followeeCount = redisTemplate.opsForZSet().zCard(followeeKey);
        return followeeCount;
    }

    /**
     * 查询某用户是否关注的另一实体
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    @Override
    public Boolean getFolloweeStatus(Integer userId, Integer entityType, Integer entityId) {
        String followeeKey = RedisUtils.getFolloweeKey(userId, entityType);
        Double score = redisTemplate.opsForZSet().score(followeeKey,entityId);
        return score != null;
    }

    /**
     * 查询某用户关注的实体集合
     * @param userId
     * @param entityType
     * @param offset
     * @param limit
     * @return
     */
    @Override
    public List<Map<String, Object>> listFollowee(Integer userId, Integer entityType, Integer offset, Integer limit) {
        String followeeKey = RedisUtils.getFolloweeKey(userId, entityType);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        if (targetIds == null) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.getById(targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

    /**
     * 查询某实体粉丝的集合
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    @Override
    public List<Map<String, Object>> listFollower(Integer entityType, Integer entityId, Integer offset, Integer limit) {
        String followerKey = RedisUtils.getFollowerKey(entityType, entityId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        if (targetIds == null) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.getById(targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followerKey, targetId);
            map.put("followTime",new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }
}
