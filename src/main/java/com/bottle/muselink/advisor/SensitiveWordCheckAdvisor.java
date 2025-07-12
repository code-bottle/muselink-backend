package com.bottle.muselink.advisor;

import com.bottle.muselink.exception.SensitiveWordException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 违禁词检查Advisor
 */
@Slf4j
public class SensitiveWordCheckAdvisor implements CallAdvisor, StreamAdvisor {
    private static final String DEFAULT_SENSITIVE_WORDS_FILE = "sensitive_words.txt";
    private final List<String> sensetiveWords;

    /**
     * 创建默认违禁词Advisor，从默认文件读取违禁词列表
     */
    public SensitiveWordCheckAdvisor() {
        this.sensetiveWords = loadSensitiveWordsFromFile(DEFAULT_SENSITIVE_WORDS_FILE);
        log.info("初始化敏感词检测拦截器，加载敏感词数量: {}", sensetiveWords.size());
    }

    /**
     * 从文件加载违禁词列表
     */
    private List<String> loadSensitiveWordsFromFile(String filePath) {
        try {
            var resource = new ClassPathResource(filePath);
            var reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

            List<String> words = reader.lines()
                    .filter(StringUtils::hasText)
                    .filter(line -> !line.trim().startsWith("#"))
                    .map(String::trim)
                    .collect(Collectors.toList());
            return words;

        } catch (Exception e) {
            log.error("加载文件失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 检查请求中是否包含违禁词
     */
    private ChatClientRequest checkRequest(ChatClientRequest request) {
        String userText = request.prompt().getUserMessage().getText().toLowerCase();
        for (String word : sensetiveWords) {
            if (userText.contains(word.toLowerCase())) {
                throw new SensitiveWordException("用户输入包含敏感内容：" + userText);
            }
        }
        return request;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        return callAdvisorChain.nextCall(checkRequest(chatClientRequest));
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        return streamAdvisorChain.nextStream(checkRequest(chatClientRequest));
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
