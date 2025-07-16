package com.bottle.muselink.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;


public class MyCustomRagAdvisorFactory {
    /**
     * 创建云端知识库 RAG 检索增强顾问
     *
     * @return 云端 RAG 检索增强顾问
     */
    public static Advisor createCloudRagCustomAdvisor() {
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .build();
        final String KNOWLEDGE_INDEX = "consultation_cases";
        DocumentRetriever documentRetriever = new DashScopeDocumentRetriever(dashScopeApi,
                DashScopeDocumentRetrieverOptions.builder()
                        .withIndexName(KNOWLEDGE_INDEX)
                        .build());
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .build();
    }
    /**
     * 创建自定义的本地知识库 RAG 检索增强顾问
     *
     * @param vectorStore 向量存储
     * @return 自定义的 RAG 检索增强顾问
     */
    public static Advisor createLocalRagCustomAdvisor(VectorStore vectorStore) {

        // 创建文档检索器
        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .similarityThreshold(0.6)
                .topK(3)
                .build();

        // 创建上下文查询增强器（允许空上下文）
        PromptTemplate emptyContextPromptTemplate = new PromptTemplate("""
                你应该输出下面的内容：
                抱歉，我只能回答心理咨询相关的问题，别的没办法帮到您哦。""");
        ContextualQueryAugmenter contextualQueryAugmenter  = ContextualQueryAugmenter.builder()
                .allowEmptyContext(false)
                .emptyContextPromptTemplate(emptyContextPromptTemplate)
                .build();

        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .queryAugmenter(contextualQueryAugmenter)
                .build();
    }
}
