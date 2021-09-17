package edu.hue.community.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author xiaoning
 * @date 2021/09/14
 *
 * 发送邮件工具类
 */
@Component
@Slf4j
public class MailUtils {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 发件人
     */
    @Value("${spring.mail.username}")
    private String addresser;

    /**
     * 发送邮件
     * @param recipients 收件人邮箱
     * @param title 邮件标题
     * @param message 邮件内容
     */
    public void sendMail(String recipients, String title, String message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            // 设置发件人
            mimeMessageHelper.setFrom(addresser);
            // 设置收件人
            mimeMessageHelper.setTo(recipients);
            // 设置邮件主题
            mimeMessageHelper.setSubject(title);
            // 设置邮件内容
            mimeMessageHelper.setText(message,true);
            // 发送邮件
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("发送邮件失败，原因：" + e.getMessage());
        }
    }

}
