package com.bottle.muselink.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MyPgVectorStoreConfigTest {
    @Resource
    VectorStore pgVectorStore;

    @Test
    void test() {
        List<Document> documents = List.of(
                new Document("焦虑症可以通过深呼吸练习、正念冥想和认知行为疗法（CBT）来缓解。每天花10分钟进行腹式呼吸训练有助于降低焦虑水平，同时记录自己的想法并重新评估负面思维模式也是有效的自我调节方式。",
                        Map.of("topic", "焦虑应对", "tags", "CBT, 呼吸训练, 正念")),

                new Document("面对抑郁情绪，建立规律作息、适度运动和保持社交联系是关键。建议每天至少外出散步30分钟，与亲友保持沟通，并尝试写日记记录情绪变化。如果持续两周以上无改善，应寻求专业心理咨询帮助。",
                        Map.of("topic", "抑郁症缓解", "tags", "运动, 社交, 日记疗法")),

                new Document("失眠问题可通过‘睡眠卫生管理’来改善：如避免晚上使用电子设备、保持卧室安静黑暗、设定固定起床时间、白天减少午睡时间等。此外，渐进式肌肉放松法和4-7-8呼吸法也有助于快速入睡。",
                        Map.of("topic", "改善睡眠", "tags", "睡眠卫生, 放松技巧")));
        pgVectorStore.add(documents);

        List<Document> results = pgVectorStore.similaritySearch(SearchRequest.builder().query("最近有点不开心").topK(2).build());
        System.out.println(results);
        Assertions.assertNotNull(results);
    }
}