package edu.hue.community.service;

/**
 * @author 47552
 * @date 2021/09/20
 * 处理与赞相关的业务
 */
public interface LikeService {

    /**
     * 更新点赞次数，如果某用户没点赞则+1，点过则-1
     * @param userId 点赞用户的id
     * @param entityType 被点赞内容的类型（帖子/评论）
     * @param entityId 被点赞内容的id
     * @param entityUserId 实体作者的id
     * @return
     */
    void updateLikeCount(Integer userId, Integer entityType, Integer entityId, Integer entityUserId);

    /**
     * 获取某实体的点赞数量
     * @param entityType 被点赞内容的类型（帖子/评论）
     * @param entityId 被点赞内容的id
     * @return
     */
    Long getLikeCount(Integer entityType, Integer entityId);

    /**
     * 查询用户是否给实体点过赞
     * @param userId 点赞用户的id
     * @param entityType 被点赞内容的类型（帖子/评论）
     * @param entityId 被点赞内容的id
     * @return
     */
    Integer getLikeStatus(Integer userId, Integer entityType, Integer entityId);

    /**
     * 通过用户id查询该用户获得的赞的数量
     * @param userId
     * @return
     */
    Integer getLikeCountByUserId(Integer userId);
}
