package edu.hue.community.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 47552
 * @date 2021/09/20
 */
public interface FollowService {

    /**
     * 设置关注信息
     * @param userId
     * @param entityType 关注的实体类型
     * @param entityId 关注的实体id
     */
    void setFollow(Integer userId, Integer entityType, Integer entityId);

    /**
     * 取消关注
     * @param userId
     * @param entityType
     * @param entityId
     */
    void removeFollow(Integer userId, Integer entityType, Integer entityId);

    /**
     * 查询用户粉丝的数量
     * @param entityType
     * @param entityId
     * @return
     */
    Long getFollowerCount(Integer entityType, Integer entityId);

    /**
     * 查询用户关注的人的数量
     * @param userId
     * @param entityType
     * @return
     */
    Long getFolloweeCount(Integer userId, Integer entityType);

    /**
     * 查询某用户是否关注的另一实体
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    Boolean getFolloweeStatus(Integer userId, Integer entityType, Integer entityId);

    /**
     * 查询某用户关注的实体集合
     * @param userId
     * @param entityType
     * @param offset
     * @param limit
     * @return
     */
    List<Map<String, Object>> listFollowee(Integer userId, Integer entityType, Integer offset, Integer limit);

    /**
     * 查询某实体粉丝的集合
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    List<Map<String, Object>> listFollower(Integer entityType, Integer entityId, Integer offset, Integer limit);

}
