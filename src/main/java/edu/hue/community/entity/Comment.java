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
 * @date 2021/09/18
 * 评论信息的实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("comment")
public class Comment {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 发贴人的id
     */
    private Integer userId;
    /**
     * 评论的类型
     */
    private Integer entityType;
    private Integer entityId;
    private Integer targetId;
    /**
     * 评论的内容
     */
    private String content;
    /**
     * 评论的状态
     */
    private Integer status;
    /**
     * 评论时间
     */
    private Date createTime;

}
