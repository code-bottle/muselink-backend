package com.bottle.muselink.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义文档读取器
 */
@Component
@Slf4j
public class MyDocumentReader {

    private final ResourcePatternResolver resourcePatternResolver;

    @Autowired
    public MyDocumentReader(ResourcePatternResolver resourcePatternResolver){
        this.resourcePatternResolver = resourcePatternResolver;
    }
    /**
     * 读取知识库文档
     * @return
     */
    public List<Document> readMarkdownFiles() {
        List<Document> allDocuments = new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:consultation_cases/*.md");
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                // 提取知识库文档的咨询主题
                String topic = filename.split("-")[0];
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .withAdditionalMetadata("filename", filename)
                        .withAdditionalMetadata("topic", topic)
                        .build();
                MarkdownDocumentReader markdownDocumentReader = new MarkdownDocumentReader(resource, config);
                allDocuments.addAll(markdownDocumentReader.get());
            }
        } catch (IOException e) {
            log.error("RAG知识库文档加载失败", e);
        }
        log.info("RAG知识库文档加载成功，共计{}个", allDocuments.size());
        return allDocuments;
    }

}
