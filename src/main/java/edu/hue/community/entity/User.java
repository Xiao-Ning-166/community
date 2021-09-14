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
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
public class User {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     *
     */
    private String salt;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户类型
     * 0-普通用户、1-超级管理员、2-版主
     */
    private Integer type;

    /**
     * 用户状态
     * 0-未激活、1-已激活
     */
    private Integer status;

    /**
     * 激活码
     */
    private String activationCode;

    /**
     * 头像地址
     */
    private String headerUrl;

    /**
     * 注册时间
     */
    private Date createTime;
}
