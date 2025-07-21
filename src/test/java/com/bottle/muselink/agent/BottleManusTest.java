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
                下周计划去广州天河区旅游，推荐附近10公里内的景点和餐厅，结合相关的网络图片帮我制作一份详细的旅游规划，以PDF格式文件输出。
                """;
        String answer = BottleManus.run(userPrompt);
        Assertions.assertNotNull(answer);
    }
}
