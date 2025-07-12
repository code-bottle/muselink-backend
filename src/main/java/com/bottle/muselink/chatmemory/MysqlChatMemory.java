package com.bottle.muselink.chatmemory;

import com.bottle.muselink.service.ChatMessageMemoryService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MysqlChatMemory implements ChatMemory {
    @Autowired
    private ChatMessageMemoryService chatMessageMemoryService;
    @Override
    public void add(String conversationId, List<Message> messages) {
        chatMessageMemoryService.addMessages(conversationId, messages);
    }

    @Override
    public List<Message> get(String conversationId) {
        return chatMessageMemoryService.getMessages(conversationId);
    }

    @Override
    public void clear(String conversationId) {
        chatMessageMemoryService.clearMessages(conversationId);
    }
}
