package edu.hue.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hue.community.dao.MessageMapper;
import edu.hue.community.entity.Message;
import edu.hue.community.service.MessageService;
import edu.hue.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

/**
 * @author 47552
 * @date 2021/09/19
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**
     * 查询用户的所有会话和每个会话对应的最新的消息
     * @param page
     * @param userId
     * @return
     */
    @Override
    public IPage<Message> listConversation(IPage<Message> page, Integer userId) {
        return messageMapper.listConversation(page, userId);
    }

    /**
     * 查询会话的所有信息
     * @param page
     * @param conversationId 会话的编号
     * @return
     */
    @Override
    public IPage<Message> listLetter(IPage<Message> page, String conversationId) {
        QueryWrapper<Message> query = new QueryWrapper();
        query.ne("status",2)
             .ne("from_id",1)
             .eq("conversation_id",conversationId).orderByDesc("id");
        IPage<Message> messagePage = this.page(page,query);
        return messagePage;
    }

    /**
     * 查询会话未读私信的数量
     * @param userId         用户id
     * @param conversationId 会话id
     * @return
     */
    @Override
    public Integer getLetterUnreadCount(Integer userId, String conversationId) {
        QueryWrapper<Message> query = new QueryWrapper();
        query.eq("status",0).ne("from_id",1)
             .eq("to_id",userId).eq(conversationId!=null,"conversation_id",conversationId);
        Integer count = messageMapper.selectCount(query);
        return count;
    }

    /**
     * 保存私信信息
     * @param message
     * @return
     */
    @Override
    public Integer insertMessage(Message message) {
        // 过滤html标签
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        // 过滤敏感词
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insert(message);
    }
}
