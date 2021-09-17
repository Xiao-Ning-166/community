package edu.hue.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hue.community.dao.LoginTicketMapper;
import edu.hue.community.entity.LoginTicket;
import edu.hue.community.service.LoginTicketService;
import org.springframework.stereotype.Service;

/**
 * @author 47552
 * @date 2021/09/15
 */
@Service
public class LoginTicketServiceImpl extends ServiceImpl<LoginTicketMapper, LoginTicket>
        implements LoginTicketService {
}
