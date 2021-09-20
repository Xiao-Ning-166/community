package edu.hue.community.util;

/**
 * @author 47552
 * @date 2021/09/20
 * Redis 相关的工具类
 */
public class RedisUtils {

    /**
     * 分隔符 ：
     */
    private static final String SPLIT = ":";
    /**
     * 点赞键的主键
     */
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";

    /**
     * 生成一个存放点赞次数的键
     * @param entityType 被点赞内容的类型
     * @param entityId 被点赞内容的id
     * @return
     */
    public static String getRedisKey(int entityType, int entityId) {
        String likeKey = PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
        return likeKey;
    }

    /**
     * 生成一个存放用户收到赞的数量的键
     * @param userId
     * @return
     */
    public static String getUserKey(Integer userId) {
        String userLikeKey = PREFIX_USER_LIKE + SPLIT + userId;
        return userLikeKey;
    }

}
