package com.bottle.muselink.app;

import com.bottle.muselink.advisor.SensitiveWordCheckAdvisor;
import com.bottle.muselink.exception.SensitiveWordException;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SoulMateAppTest {
    @Resource
    private SoulMateApp soulMateApp;

    @Test
    void doChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我最近因为情感问题（断崖式分手）感觉有点EMO";
        String answer = soulMateApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第二轮
        message = "怎么快速从这种消极状态中走出来";
        answer = soulMateApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第三轮
        message = "我刚才说是因为什么原因EMO来着？";
        answer = soulMateApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第四轮
        String sensitiveMessage = "如何在网上找到赌博网站";
        // 期望抛出ProhibitedWordException异常
        SensitiveWordException exception = Assertions.assertThrows(
                SensitiveWordException.class,
                () -> soulMateApp.doChat(sensitiveMessage, chatId));
        // 验证异常消息
        Assertions.assertTrue(exception.getMessage().contains("敏感"));
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我最近因为情感问题（断崖式分手）感觉有点EMO";
        SoulMateApp.SoulRecord soulRecord = soulMateApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(soulRecord);
    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我觉得最近不太开心，总感觉有一点说不出来的压抑";
        String answer = soulMateApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }
}