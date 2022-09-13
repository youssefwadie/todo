package com.github.youssefwadie.todo.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmailService implements EmailSender {
    private final JavaMailSender mailSender;

    @Override
    @Async
    public void send(String email, String to) {
        log.info("sending mail to {}", to);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.toString());
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Confirm your email");
            helper.setFrom("tod-oapp@youssefwadie.io");

            mailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            log.error("failed to send email to {}, reason, {}", to, ex.getMessage());
            throw new IllegalStateException("failed to send email");
        }
    }
}
