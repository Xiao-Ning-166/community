package edu.hue.community.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.hue.community.entity.Message;
import edu.hue.community.entity.User;
import edu.hue.community.service.MessageService;
import edu.hue.community.service.UserService;
import edu.hue.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return "/site/letter-detail";
    }

}
