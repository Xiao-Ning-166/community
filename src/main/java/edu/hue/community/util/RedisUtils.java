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
    /**
     * 用户收到点赞数的前缀
     */
    private static final String PREFIX_USER_LIKE = "like:user";
    /**
     * 实体的粉丝的前缀
     */
    private static final String PREFIX_FOLLOWER = "follower";
    /**
     * 用户关注的实体的前缀
     */
    private static final String PREFIX_FOLLOWEE = "followee";
    /**
     * 登录验证码的前缀
     */
    private static final String PREFIX_LOGIN_CODE = "code:login";
    /**
     * 登录凭证的前缀
     */
    private static final String PREFIX_LOGIN_TICKET = "ticket:login";
    /**
     * 缓存用户数据的前缀
     */
    private static final String PREFIX_CACHE_USER = "cache:user";

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

    /**
     * 得到存放某个实体的粉丝的键
     * @return
     */
    public static String getFollowerKey(Integer entityType, Integer entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 得到某用户关注的实体的键
     * @param userId
     * @param entityType
     * @return
     */
    public static String getFolloweeKey(Integer userId, Integer entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 得到存放登录验证码的key
     * @param owner
     * @return
     */
    public static String getLoginCodeKey(String owner) {
        return PREFIX_LOGIN_CODE + SPLIT + owner;
    }

    /**
     * 得到存放登录凭证的key
     * @param ticket
     * @return
     */
    public static String getLoginTicketKey(String ticket) {
        return PREFIX_LOGIN_TICKET + SPLIT + ticket;
    }

    /**
     * 得到存放缓存用户数据的key
     * @param userId
     * @return
     */
    public static String getCacheUserKey(Integer userId) {
        return PREFIX_CACHE_USER + SPLIT + userId;
    }
}
