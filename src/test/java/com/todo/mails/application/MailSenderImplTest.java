package com.todo.registering.mails.application;

import com.todo.mails.application.MailSenderImpl;
import com.todo.mails.domain.Email;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class MailSenderImplTest {

    @Autowired
    private MailSenderImpl mailSenderImpl;

    @Test
    void shouldSendMail(){
        mailSenderImpl.sendEmail(new Email( "test@test", "test", "test", "test"));

    }


}