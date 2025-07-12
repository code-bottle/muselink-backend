package com.bottle.muselink.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bottle.muselink.domain.ChatMessageMemory;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

/**
* @description 针对表【chat_message_memory(AI会话消息表)】的数据库操作Service
*/
public interface ChatMessageMemoryService extends IService<ChatMessageMemory> {
    /**
     * 添加多条消息
     *
     * @param conversationId 会话ID
     * @param messages       消息列表
     */
    void addMessages(String conversationId, List<Message> messages);

    /**
     * 获取会话消息
     *
     * @param conversationId 会话ID
     * @return 消息列表
     */
    List<Message> getMessages(String conversationId);

    /**
     * 清除会话消息（逻辑删除）
     *
     * @param conversationId 会话ID
     */
    void clearMessages(String conversationId);
}
