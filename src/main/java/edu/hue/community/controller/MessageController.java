package edu.hue.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.hue.community.entity.Message;
import edu.hue.community.entity.User;
import edu.hue.community.service.MessageService;
import edu.hue.community.service.UserService;
import edu.hue.community.util.HostHolder;
import edu.hue.community.util.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
     * 去往消息页面
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
                User target = userService.getById(targetId);
                map.put("target", target);

                conversationVoList.add(map);
            }
        }
        model.addAttribute("conversationVoList", conversationVoList);
        // 查询未读消息总数
        Integer unreadTotal = messageService.getLetterUnreadCount(user.getId(), null);
        model.addAttribute("unreadTotal",unreadTotal);
        model.addAttribute("messagePage",messagePage);
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
                map.put("fromUser", userService.getById(message.getFromId()));

                letters.add(map);
            }
        }
        Message letter = letterList.get(0);
        Integer targetId = user.getId().equals(letter.getFromId()) ? letter.getToId() : letter.getFromId();
        User target = userService.getById(targetId);
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
}
