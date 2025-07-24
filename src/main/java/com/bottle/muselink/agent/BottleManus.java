package com.bottle.muselink.agent;


import com.bottle.muselink.advisor.AppSpecificLoggerAdvisor;
import com.bottle.muselink.advisor.SensitiveWordCheckAdvisor;
import com.bottle.muselink.chatmemory.MysqlChatMemory;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Bottle出品：通用智能体
 */
@Component
public class BottleManus extends ToolCallAgent {


    public BottleManus(ToolCallback[] allTools, ToolCallbackProvider toolCallbackProvider, ChatModel dashscopeChatModel, MysqlChatMemory mysqlChatMemory) {
        super(allTools, toolCallbackProvider);
        this.setName("BottleManus");
        String SYSTEM_PROMPT = """
                You are BottleManus, an all-capable AI assistant, aimed at solving any task presented by the user.
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);
        String NEXT_STEP_PROMPT = """
                Based on user needs, proactively select the most appropriate tool or combination of tools.
                For complex tasks, you can break down the problem and use different tools step by step to solve it.
                After using each tool, clearly explain the execution results and suggest the next steps.
                If you want to stop the interaction at any point, use the `terminate` tool/function call.
                """;
        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        this.setMaxSteps(20);
        // 初始化 AI 对话客户端
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                // 针对输入内容的敏感词检测
                .defaultAdvisors(new SensitiveWordCheckAdvisor())
                // 基于MySQL的会话记忆
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(mysqlChatMemory).build())
                // 自定义Logger的Advisor
                .defaultAdvisors(new AppSpecificLoggerAdvisor())
                .build();
        this.setChatClient(chatClient);
    }
}
