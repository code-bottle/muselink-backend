package com.bottle.muselink.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义关键词元信息增强器
 */
@Component
public class MyKeywordMetadataEnricher {
    @Autowired
    private ChatModel dashscopeChatModel;

    /**
     * 为文档列表添加关键词元信息，提升可搜索性
     *
     * @param documents 待增强的文档列表
     * @return 增强后的文档列表
     */
    public List<Document> enrichDocuments(List<Document> documents) {
        KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(dashscopeChatModel, 3);
        return keywordMetadataEnricher.apply(documents);
    }
}