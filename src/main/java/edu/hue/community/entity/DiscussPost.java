package edu.hue.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

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
@Document(indexName = "discusspost", shards = 6, replicas = 3)
//@Mapping(mappingPath = "discusspost.json")
public class DiscussPost {
    /**
     * 讨论帖的主键
     */
    @TableId(type = IdType.AUTO)
    @Id
    private Integer id;
    /**
     * 发帖用户的id
     */
    @Field(type = FieldType.Integer/*name = "userId"*/)
    private Integer userId;
    /**
     * 帖子的主题
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart"/*name = "title"*/)
    private String title;
    /**
     * 帖子的内容
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart"/*name = "content"*/)
    private String content;
    /**
     * 帖子是置顶
     * 0-普通; 1-置顶
     */
    @Field(type = FieldType.Integer/*name = "type"*/)
    private Integer type;
    /**
     * 帖子的状态
     * 0-正常; 1-精华; 2-拉黑;
     */
    @Field(type = FieldType.Integer/*name = "status"*/)
    private Integer status;
    /**
     * 帖子创建的时间
     */
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis")
    private Date createTime;
    /**
     * 帖子的评论数量
     */
    @Field(type = FieldType.Integer/*name = "commentCount"*/)
    private Integer commentCount;
    /**
     * 帖子的分数
     */
    @Field(type = FieldType.Double/*name = "score"*/)
    private Double score;

}
