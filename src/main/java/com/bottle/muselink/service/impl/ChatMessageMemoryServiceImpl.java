package com.bottle.muselink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bottle.muselink.domain.ChatMessageMemory;
import com.bottle.muselink.mapper.ChatMessageMemoryMapper;
import com.bottle.muselink.service.ChatMessageMemoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

/**
* @description 针对表【chat_message_memory(AI会话消息表)】的数据库操作Service实现
*/
@Slf4j
@Service
public class ChatMessageMemoryServiceImpl extends ServiceImpl<ChatMessageMemoryMapper, ChatMessageMemory>
    implements ChatMessageMemoryService {

    @Override
    @Transactional
    public void addMessages(String conversationId, List<Message> messages) {
        if (messages == null || messages.isEmpty() || conversationId == null){
            log.info("Invalid conversationId or messages");
        }
        // 获取当前最大序号
        Integer maxOrder = baseMapper.getMaxOrder(conversationId);
        int nextOrder = (maxOrder != null ? maxOrder : 0) + 1;
        // 将SpringAI消息转换为实体
        List<ChatMessageMemory> entities = IntStream.range(0, messages.size())
                .mapToObj(i -> {
                    Message message = messages.get(i);
                    int order = nextOrder + i;
                    return transformToChatMessageMemory(message, conversationId, order);
                })
                .toList();
        // 批量保存
        saveBatch(entities);
        log.info("已添加 {} 条消息到会话 {}", messages.size(), conversationId);
    }

    @Override
    public List<Message> getMessages(String conversationId) {
        LambdaQueryWrapper<ChatMessageMemory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessageMemory::getConversationId, conversationId)
                .eq(ChatMessageMemory::getIsDeleted, 0)
                .orderByDesc(ChatMessageMemory::getMessageOrder);

        List<Message> messages = list(wrapper)
                .stream()
                .map(this::transformFormChatMessageMemory)
                .toList();

        log.info("已从会话 {} 中检索到 {} 条消息", conversationId, messages.size());
        return messages;
    }

    @Override
    @Transactional
    public void clearMessages(String conversationId) {
        // 逻辑删除所有会话消息
        int count = baseMapper.logicalDeleteByConversationId(conversationId);
        log.info("已从会话 {} 中逻辑删除 {} 条消息", conversationId, count);
    }

    public ChatMessageMemory transformToChatMessageMemory(Message message, String conversationId, int order){
        return  ChatMessageMemory.builder()
                .userId(1L)
                .conversationId(conversationId)
                .messageOrder(order)
                .messageType(message.getMessageType().toString())
                .messageContent(message.getText())
                .createTime(new Date())
                .updateTime(new Date())
                .isDeleted(0)
                .build();
    }

    public Message transformFormChatMessageMemory(ChatMessageMemory entity){
        String messageType = entity.getMessageType();
        String messageContent = entity.getMessageContent();

        // 基于消息类型创建相应的消息实例
        switch (messageType) {
            case "USER":
                return new UserMessage(messageContent);
            case "ASSISTANT":
                return new AssistantMessage(messageContent);
            case "SYSTEM":
                return new SystemMessage(messageContent);
            default:
                log.warn("未知的消息类型: {}", messageType);
                return new AssistantMessage("未知消息类型: " + messageContent);
        }
    }

}




