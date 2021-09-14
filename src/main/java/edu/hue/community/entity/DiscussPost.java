package edu.hue.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author 47552
 * @date 2021/09/12
 *
 * 讨论帖 实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("discuss_post")
public class DiscussPost {
    /**
     * 讨论帖的主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 发帖用户的id
     */
    private Integer userId;
    /**
     * 帖子的主题
     */
    private String title;
    /**
     * 帖子的内容
     */
    private String content;
    /**
     * 帖子是置顶
     * 0-普通; 1-置顶
     */
    private Integer type;
    /**
     * 帖子的状态
     * 0-正常; 1-精华; 2-拉黑;
     */
    private Integer status;
    /**
     * 帖子创建的时间
     */
    private Date createTime;
    /**
     * 帖子的评论数量
     */
    private Integer commentCount;
    /**
     * 帖子的分数
     */
    private Double score;

}
