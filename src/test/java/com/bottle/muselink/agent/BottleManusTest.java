package com.bottle.muselink.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class BottleManusTest {
    @Resource
    private BottleManus BottleManus;

    @Test
    void run() {
        BottleManus.startNewConversation();
        String userPrompt = """  
                下个礼拜计划去北京旅游2天，求推荐3个著名景点，
                结合相关网络图片，帮我制作一份详细的旅游规划，以PDF格式文件输出。
                """;
        String answer = BottleManus.run(userPrompt);
        Assertions.assertNotNull(answer);
    }
}
