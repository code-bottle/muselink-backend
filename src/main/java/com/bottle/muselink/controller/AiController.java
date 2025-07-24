package com.bottle.muselink.controller;

import com.bottle.muselink.agent.BottleManus;
import com.bottle.muselink.app.SoulMateApp;
import com.bottle.muselink.chatmemory.MysqlChatMemory;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private SoulMateApp soulMateApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    @Resource
    private ChatModel dashscopeChatModel;

    @Resource
    private MysqlChatMemory mysqlChatMemory;

    /**
     * 流式调用BottlManus智能体
     *
     * @param message
     * @return
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        BottleManus bottleManus = new BottleManus(allTools, toolCallbackProvider, dashscopeChatModel, mysqlChatMemory);
        bottleManus.startNewConversation();
        return bottleManus.runStream(message);
    }

    /**
     * 流式调用心理咨询对话
     * @param message
     * @param chatId
     * @param session
     * @return
     */
    @GetMapping(value = "/soul_mate_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatSseWithSoulMateApp(String message, String chatId, HttpSession session) {
        chatId = resolveChatId(session, chatId);
        return soulMateApp.doChatByStream(message, chatId);
    }

    @GetMapping("/soul_mate_app/chat/sync")
    public String doSyncChatWithSoulMateApp(String message, String chatId, HttpSession session) {
        chatId = resolveChatId(session, chatId);
        return soulMateApp.doChat(message, chatId);
    }

    private String resolveChatId(HttpSession session, String chatId) {
        if (chatId == null || chatId.trim().isEmpty()) {
            Object sessionChatId = session.getAttribute("chatId");
            if (sessionChatId == null) {
                chatId = UUID.randomUUID().toString();
                session.setAttribute("chatId", chatId);
            } else {
                chatId = sessionChatId.toString();
            }
        }
        return chatId;
    }

    // @GetMapping(value = "/soul_mate_app/chat/sse")
    // public Flux<ServerSentEvent<String>> doChatSseWithSoulMateApp(String message, String chatId) {
    //     return soulMateApp.doChatByStream(message, chatId)
    //             .map(chunk -> ServerSentEvent.<String>builder()
    //                     .data(chunk)
    //                     .build());
    // }

    @GetMapping("/soul_mate_app/chat/sse_emitter")
    public SseEmitter doChatSseEmitterWithSoulMateApp(String message, String chatId, HttpSession session) {
        chatId = resolveChatId(session, chatId);
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter emitter = new SseEmitter(180000L); // 3分钟超时
        // 获取 Flux 数据流并直接订阅
        soulMateApp.doChatByStream(message, chatId)
                .subscribe(
                        // 处理每条消息
                        chunk -> {
                            try {
                                emitter.send(chunk);
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        // 处理错误
                        emitter::completeWithError,
                        // 处理完成
                        emitter::complete
                );
        // 返回emitter
        return emitter;
    }



}

