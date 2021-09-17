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
 * @date 2021/09/15
 * 登录凭证
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("login_ticket")
public class LoginTicket {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 登录凭证
     */
    private String ticket;
    /**
     * 凭证是否有效
     * 0-有效; 1-无效;
     */
    private Integer status;
    /**
     * 凭证过期时间
     */
    private Date expired;
}
