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
 * @date 2021/09/19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("message")
public class Message {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 发消息的用户的id
     * 1：表示系统消息
     */
    private Integer fromId;
    /**
     * 收消息的用户的id
     */
    private Integer toId;
    /**
     * 会话id
     * 小id_大id
     */
    private String conversationId;
    /**
     * 消息的内容
     */
    private String content;
    /**
     * 消息的状态
     * '0-未读;1-已读;2-删除;'
     */
    private Integer status;
    /**
     * 发送消息的时间
     */
    private Date createTime;

}
