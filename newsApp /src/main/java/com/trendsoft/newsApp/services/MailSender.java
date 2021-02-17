package com.trendsoft.newsApp.services;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class MailSender {
   JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String username;

  public MailSender(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }
  public MailSender(){}

  /**
   * отправка сообщения на электронную почту пользователя, используется для восстановление пароля
   */
  @SneakyThrows
  public void send(String emailTo, String subject, String text) {
    MimeMessage mimeMessage = mailSender.createMimeMessage();

    MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
    message.setFrom(username);
    message.setTo(emailTo);
    message.setSubject(subject);
    message.setText(text, true);

    mailSender.send(mimeMessage);
  }
}
