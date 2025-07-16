package com.bottle.muselink.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义分词器
 */
@Component
class MyTokenSplitter {
    /**
     * 默认的分词器
     *
     * @param documents
     * @return
     */
    public List<Document> splitDocuments(List<Document> documents) {
        TokenTextSplitter splitter = new TokenTextSplitter();
        return splitter.apply(documents);
    }
    /**
     * 自定义分词器
     *
     * @param documents
     * @return
     */
    public List<Document> splitCustomized(List<Document> documents) {
        TokenTextSplitter splitter = new TokenTextSplitter(500, 100, 10, 5000, true);
        return splitter.apply(documents);
    }
}

