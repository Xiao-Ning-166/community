package edu.hue.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.hue.community.entity.Comment;
import edu.hue.community.entity.Message;
import edu.hue.community.entity.User;
import edu.hue.community.service.MessageService;
import edu.hue.community.service.UserService;
import edu.hue.community.util.HostHolder;
import edu.hue.community.util.JSONUtils;
import edu.hue.community.util.MessageConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @author 47552
 * @date 2021/09/19
 */
@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 去往私信页面
     * @return
     */
    @GetMapping("/letter")
    public String getLetterList(Model model,
                                @RequestParam(value = "current", required = false, defaultValue = "0") Integer current) {
        User user = hostHolder.getUser();
        IPage<Message> page = new Page<>(current, 5);
        IPage<Message> messagePage = messageService.listConversation(page, user.getId());
        List<Message> conversationList = messagePage.getRecords();
        // 封装会话信息
        List<Map<String, Object>> conversationVoList = new ArrayList<>();
        if (conversationList != null) {
            for (Message conversation : conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", conversation);
                // 会话中未读消息数量
                map.put("unreadCount", messageService.getLetterUnreadCount(user.getId(),conversation.getConversationId()));
                // 会话中消息总数
                IPage page02 = new Page(0,20);
                IPage<Message> letterPage = messageService.listLetter(page02, conversation.getConversationId());
                map.put("messageCount",letterPage.getTotal());
                // 得到对方的id
                Integer targetId = user.getId().equals(conversation.getFromId()) ? conversation.getToId() : conversation.getFromId();
                User target = userService.getUserById(targetId);
                map.put("target", target);

                conversationVoList.add(map);
            }
        }
        model.addAttribute("conversationVoList", conversationVoList);
        // 查询未读消息总数
        Integer letterUnreadCount = messageService.getLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        model.addAttribute("messagePage",messagePage);

        // 通知未读数量
        Integer noticeUnreadCount = messageService.getNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);
        return "site/letter";
    }

    /**
     * 私信详情页面
     * @param model
     * @param conversationId
     * @param current
     * @return
     */
    @GetMapping("/getdetail/{conversationId}")
    public String getdetail(Model model,
                            @PathVariable("conversationId") String conversationId,
                            @RequestParam(value = "current", required = false, defaultValue = "0") Integer current) {
        User user = hostHolder.getUser();
        IPage<Message> page = new Page<>(current,5);
        IPage<Message> letterPage = messageService.listLetter(page, conversationId);

        // 私信列表
        List<Message> letterList = letterPage.getRecords();
        List<Map<String, Object>> letters = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.getUserById(message.getFromId()));

                letters.add(map);
            }
        }
        Message letter = letterList.get(0);
        Integer targetId = user.getId().equals(letter.getFromId()) ? letter.getToId() : letter.getFromId();
        User target = userService.getUserById(targetId);
        model.addAttribute("letters",letters);
        model.addAttribute("targetUser",target);
        model.addAttribute("page",letterPage);
        model.addAttribute("conversationId",conversationId);

        // 更新未读数据为已读
        List<Integer> ids = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                if (user.getId().equals(message.getToId()) && message.getStatus().equals(0)) {
                    ids.add(message.getId());
                }
            }
        }
        if (!ids.isEmpty()) {
            UpdateWrapper<Message> update = new UpdateWrapper<>();
            update.set("status", 1).in("id", ids);
            messageService.update(update);
        }

        return "/site/letter-detail";
    }

    /**
     * 发送私信
     * @param username 接收私信的用户名
     * @param content  私信内容
     * @return
     */
    @PostMapping("/sendLetter")
    @ResponseBody
    public String sendLetter(String username, String content) {
        // 获取接收私信的用户信息
        QueryWrapper<User> query = new QueryWrapper();
        query.eq("username",username);
        User targetUser = userService.getOne(query);

        if (targetUser == null) {
            return JSONUtils.getJSONString(604,"目标用户不存在！！！");
        }

        // 获取发送私信的用户信息
        User user = hostHolder.getUser();

        // 封装私信信息
        Message message = new Message();
        message.setFromId(user.getId());
        message.setToId(targetUser.getId());
        if (user.getId() < targetUser.getId()) {
            message.setConversationId(user.getId() + "_" + targetUser.getId());
        } else {
            message.setConversationId(targetUser.getId() + "_" + user.getId());
        }
        message.setCreateTime(new Date());
        message.setContent(content);

        // 保存私信信息
        messageService.insertMessage(message);

        return JSONUtils.getJSONString(200,"私信已发送！！！");
    }

    @GetMapping("/deleteLetter/{letterId}")
    @ResponseBody
    public String deleteLetter(@PathVariable("letterId") Integer letterId) {
        if (letterId != null) {
            UpdateWrapper<Message> update = new UpdateWrapper<>();
            update.set("status",2).eq("id",letterId);
            messageService.update(update);
        }
        return JSONUtils.getJSONString(200);
    }

    @GetMapping("/notice")
    public String noticeList(Model model) {
        User user = hostHolder.getUser();
        IPage<Message> page = new Page<>(0,Integer.MAX_VALUE);
        // 评论通知
        Map<String, Object> commentMap = getNotice(page, user.getId(), MessageConstant.TOPIC_COMMENT);
        model.addAttribute("commentNotice",commentMap);
        // 点赞通知
        Map<String, Object> likeNotice = getNotice(page, user.getId(), MessageConstant.TOPIC_LIKE);
        model.addAttribute("likeNotice",likeNotice);
        // 关注通知
        Map<String, Object> followNotice = getNotice(page, user.getId(), MessageConstant.TOPIC_FOLLOW);
        model.addAttribute("followNotice",followNotice);

        // 未读私信数量
        Integer letterUnreadCount = messageService.getLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        // 通知未读数量
        Integer noticeUnreadCount = messageService.getNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        return "/site/notice";
    }

    private Map<String, Object> getNotice(IPage<Message> page, Integer userId, String topic) {
        // 查询通知
        IPage<Message> messagePage = messageService.listMessage(page, userId, topic);
        List<Message> messageList = messagePage.getRecords();
        Map<String, Object> messageMap = new HashMap<>();
        if (messageList != null && messageList.size() > 0) {
            Message latestMessage = messageList.get(0);
            // 最新一条数据
            messageMap.put("latestMessage", latestMessage);
            String content = HtmlUtils.htmlUnescape(latestMessage.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            // 触发事件人的信息
            messageMap.put("user",userService.getUserById((Integer) data.get("userId")));
            // 实体类型
            messageMap.put("entityType",data.get("entityType"));
            // 实体id
            messageMap.put("entityId",data.get("entityId"));
            if (data.containsKey("postId")) {
                // 所在帖子id
                messageMap.put("postId", data.get("postId"));
            }
            // 通知总数
            messageMap.put("count",messagePage.getTotal());
            // 未读的通知总数
            messageMap.put("unreadCount",messageService.getNoticeUnreadCount(userId,topic));

        }
        return messageMap;
    }

    /**
     * 获取系统通知详情
     * @param model
     * @param topic
     * @param current
     * @return
     */
    @GetMapping("/notice/{topic}")
    public String noticeDetail(Model model,
                                @PathVariable("topic") String topic,
                                @RequestParam(value = "current", required = false, defaultValue = "0") Integer current) {
        User user = hostHolder.getUser();
        IPage<Message> page = new Page<>(current,5);
        IPage<Message> messagePage = messageService.listMessage(page, user.getId(), topic);
        List<Message> messageList = messagePage.getRecords();
        if (messageList != null) {
            List<Map<String, Object>> noticeList = new ArrayList<>();
            for (Message message : messageList) {
                Map<String, Object> notice = new HashMap<>();
                // 通知信息
                notice.put("message", message);
                // 内容
                String content = HtmlUtils.htmlUnescape(message.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                notice.put("user",userService.getUserById((Integer) data.get("userId")));
                notice.put("entityType", data.get("entityType"));
                notice.put("entityId", data.get("entityId"));
                notice.put("postId", data.get("postId"));
                // 系统用户
                notice.put("system",userService.getUserById(message.getFromId()));

                noticeList.add(notice);
            }
            model.addAttribute("noticeList",noticeList);
        }
        model.addAttribute("page",messagePage);
        // 设置已读
        // 更新未读数据为已读
        List<Integer> ids = new ArrayList<>();
        if (messageList != null) {
            for (Message message : messageList) {
                if (user.getId().equals(message.getToId()) && message.getStatus().equals(0)) {
                    ids.add(message.getId());
                }
            }
        }
        if (!ids.isEmpty()) {
            UpdateWrapper<Message> update = new UpdateWrapper<>();
            update.set("status", 1).in("id", ids);
            messageService.update(update);
        }

        return "/site/notice-detail";
    }
}
