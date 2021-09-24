package edu.hue.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.hue.community.entity.Message;

/**
 * @author 47552
 * @date 2021/09/19
 */
public interface MessageService extends IService<Message> {

    /**
     * 查询用户的所有会话和每个会话对应的最新的消息
     * @param page
     * @param userId
     * @return
     */
    IPage<Message> listConversation(IPage<Message> page, Integer userId);

    /**
     * 查询会话的所有信息
     * @param page
     * @param conversationId 会话的编号
     * @return
     */
    IPage<Message> listLetter(IPage<Message> page, String conversationId);

    /**
     * 查询会话未读私信的数量
     * @param userId 用户id
     * @param conversationId 会话id
     * @return
     */
    Integer getLetterUnreadCount(Integer userId, String conversationId);

    /**
     * 保存私信信息
     * @param message
     * @return
     */
    Integer insertMessage(Message message);

    /**
     * 通过通知类型查询通知
     * @param topic
     * @return
     */
    IPage<Message> listMessage(IPage<Message> page, Integer userId, String topic);

    /**
     * 查询未读的通知数量
     * @return
     */
    Integer getNoticeUnreadCount(Integer userId, String topic);

}
