package com.bottle.muselink.app;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.bottle.muselink.advisor.AppSpecificLoggerAdvisor;
import com.bottle.muselink.advisor.SensitiveWordCheckAdvisor;
import com.bottle.muselink.chatmemory.MysqlChatMemory;
import com.bottle.muselink.rag.MyCustomRagAdvisorFactory;
import com.bottle.muselink.rag.MyQueryRewriter;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SoulMateApp {
    private static final String SYSTEM_PROMPT =
            "你是一位专业的心理咨询师，你的任务是陪伴用户探索内心世界，理解他们的情绪、想法和困扰。请始终以温暖、真诚、耐心的态度与用户对话。" +
            "你需要做到回答保持简洁以及如下要求：\n" +
            "1.主动倾听：对用户的表达给予充分的理解和反馈\n" +
            "2.共情回应：用温和的语言表达对用户情绪的感知和接纳\n" +
            "3.提问引导：通过开放性问题了解用户的经历、感受和需求\n" +
            "4.逐步深入：根据用户的回答，层层递进地挖掘背后的原因\n" +
            "5.不急于给建议：先帮助用户厘清问题本身，再提供合适的建议\n" +
            "6.正向鼓励：肯定用户的表达勇气，给予积极的心理支持\n" +
            "7.识别危机信号：若用户出现极端情绪或行为倾向，请及时提醒并建议寻求专业帮助\n" +
            "8.保持边界感：你是心理陪伴者，不是医生，不替代专业治疗\n" +
            "示例语气风格：\n" +
            "1.“听起来你最近经历了一些压力……能多说说吗？”\n" +
            "2.“我注意到你说自己‘总是失败’，这种感觉是从什么时候开始的呢？”\n" +
            "3.“这确实是一个让人感到无助的情况，你能告诉我现在最困扰你的是什么吗？”\n" +
            "4.“谢谢你愿意分享这些，我在这里陪着你。”\n" +
            "请始终以温柔而有力量的方式陪伴用户，帮助他们找到属于自己的答案。";

    private final ChatClient dashScopeChatClient;

    // @Resource
    // private VectorStore pgVectorStore;

    @Resource
    private MyQueryRewriter myQueryRewriter;

    /**
     * 构造函数，初始化 ChatClient
     *
     * @param chatClientBuilder
     */
    public SoulMateApp(ChatClient.Builder chatClientBuilder, MysqlChatMemory mysqlChatMemory) {
        // 初始化基于内存的对话记忆
        // MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
        //         .chatMemoryRepository(new InMemoryChatMemoryRepository())
        //         .maxMessages(10)
        //         .build();
        // 内存记忆的Advisor
        // .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
        this.dashScopeChatClient = chatClientBuilder
                // 系统提示词
                .defaultSystem(SYSTEM_PROMPT)
                // 针对输入内容的敏感词检测
                .defaultAdvisors(new SensitiveWordCheckAdvisor())
                // 基于MySQL的会话记忆
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(mysqlChatMemory).build())
                // 自定义Logger的Advisor
                .defaultAdvisors(new AppSpecificLoggerAdvisor())
                .build();
    }

    /**
     * AI 基础对话（支持多轮对话记忆）
     *
     * @param message
     * @param chatId
     * @return
     *
     */
    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = dashScopeChatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        return content;
    }

    record SoulRecord(String title, List<String> sugesstions){}
    /**
     * AI对话获取咨询报告
     *
     * @param message
     * @param chatId
     * @return
     */
    public SoulRecord doChatWithReport(String message, String chatId){
        SoulRecord soulRecord = dashScopeChatClient
                .prompt()
                .system(SYSTEM_PROMPT + "对话结束后生成心理咨询报告，标题为{用户名}的心理咨询报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .entity(SoulRecord.class);
        return soulRecord;
    }

    /**
     * AI对话，支持RAG检索
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRag(String message, String chatId) {
        // String rewriteQuery = myQueryRewriter.doQueryRewrite(message);
        ChatResponse chatResponse = dashScopeChatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                //  添加云端知识库RAG检索增强服务
                .advisors(MyCustomRagAdvisorFactory.createCloudRagCustomAdvisor())
                //  添加本地知识库RAG检索增强服务
                // .advisors(MyCustomRagAdvisorFactory.createLocalRagCustomAdvisor(pgVectorStore))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        return content;
    }

}
