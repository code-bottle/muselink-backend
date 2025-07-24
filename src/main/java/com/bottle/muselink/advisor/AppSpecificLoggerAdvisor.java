package com.bottle.muselink.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义日志打印拦截器
 */
@Slf4j
public class AppSpecificLoggerAdvisor implements CallAdvisor, StreamAdvisor {
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private ChatClientRequest before(ChatClientRequest request) {
        List<Message> allMessages = request.prompt().getInstructions();
        List<Message> currentRoundMessages = getCurrentUserMessages(allMessages);

        currentRoundMessages.forEach(message ->
                log.info("AI Request [{}]: {}", message.getMessageType(), message.getText()));
        return request;
    }

    private List<Message> getCurrentUserMessages(List<Message> allMessages) {
        List<Message> result = new ArrayList<>();
        int size = allMessages.size();

        for (int i = size - 1; i >= 0; i--) {
            Message message = allMessages.get(i);
            if (!(message instanceof UserMessage)) {
                break; // 遇到非用户消息，终止
            }
            result.add(0, message); // 插入到头部，保持原始顺序
        }

        return result;
    }


    private void observeAfter(ChatClientResponse chatClientResponse) {
        chatClientResponse.chatResponse().getResults().forEach(generation -> {
                    Message message = generation.getOutput();
                    log.info("AI Response [{}]: {}", message.getMessageType(), message.getText());
                });
    }
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        chatClientRequest = before(chatClientRequest);
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        observeAfter(chatClientResponse);
        return chatClientResponse;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        chatClientRequest = before(chatClientRequest);
        Flux<ChatClientResponse> chatClientResponseFlux = streamAdvisorChain.nextStream(chatClientRequest);
        return (new ChatClientMessageAggregator()).aggregateChatClientResponse(chatClientResponseFlux, this::observeAfter);
    }
}
