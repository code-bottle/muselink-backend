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

    private void testChatWithTools(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = soulMateApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
        testChatWithTools("能联网搜索下然后推荐目前有助于缓解焦虑的心理学书籍吗？");

        // 测试网页抓取：情感支持案例分析
        testChatWithTools("我和伴侣之间的沟通出现了障碍，看看心理咨询服务网站（psychologytoday.com）上的专家是如何建议处理这种情况的？");

        // 测试资源下载：音频下载
        testChatWithTools("下载一张有浪漫星空的图片帮我放松缓解压力");

        // 测试终端操作：执行代码
        testChatWithTools("编写一段Python代码打印爱心型的哈哈哈，然后执行代码返回结果");

        // 测试文件操作：保存用户档案
        testChatWithTools("保存我的聊天记录为文件，记录下每天的心情变化以及应对策略。");

        // 测试 PDF 生成
        testChatWithTools("创建一份‘个人成长与心理健康’PDF指南，内容包括每日正念练习、情绪管理技巧及求助资源列表。");
    }

    @Test
    void doChatWithMcp() {
        String chatId1 = UUID.randomUUID().toString();
        String message1 = "搜索2张星空的图片给我";
        String answer1 =  soulMateApp.doChatWithMcp(message1, chatId1);
        Assertions.assertNotNull(answer1);

        // String chatId2 = UUID.randomUUID().toString();
        // String message2 = "深圳南山区附近10公里内的游玩景点";
        // String answer2 =  soulMateApp.doChatWithMcp(message2, chatId2);
        // Assertions.assertNotNull(answer2);
    }

}