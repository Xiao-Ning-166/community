package edu.hue.community.util;

/**
 * @author 47552
 * @date 2021/09/15
 * 信息的常量类
 */
public class MessageConstant {

    /**
     * 激活成功
     */
    public static final Integer ACTIVATE_SUCCESS = 0;

    /**
     * 重复激活
     */
    public static final Integer ACTIVATE_REUSE = 1;

    /**
     * 激活失败
     */
    public static final Integer ACTIVATE_FAIL = 2;

    /**
     * 默认状态的登录凭证超时时间。默认：1小时
     */
    public static final Integer DEFAULT_TIMEOUT = 1 * 60 * 60;

    /**
     * 记住我时的登录凭证超时时间。30天
     */
    public static final Integer REMEMBER_ME_TIMEOUT = 30 * 24 * 60 * 60;

    /**
     * 实体的类型：帖子
     */
    public static final Integer ENTITY_TYPE_POST = 1;

    /**
     * 实体的类型：评论
     */
    public static final Integer ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体的类型：用户
     */
    public static final Integer ENTITY_TYPE_USER = 3;

    /**
     * 主题类型：评论
     */
    public static final String TOPIC_COMMENT = "comment";
    /**
     * 主题类型：点赞
     */
    public static final String TOPIC_LIKE = "like";
    /**
     * 点赞
     */
    public static final String TOPIC_FOLLOW = "follow";
    /**
     * 系统用户的id
     */
    public static final Integer SYSTEM_USER_ID = 1;
}
