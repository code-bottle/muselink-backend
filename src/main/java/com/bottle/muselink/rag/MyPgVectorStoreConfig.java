package com.bottle.muselink.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

@Configuration
public class MyPgVectorStoreConfig {

    @Autowired
    private MyDocumentReader myDocumentReader;

    @Autowired
    private MyTokenSplitter myTokenSplitter;

    @Autowired
    private MyKeywordMetadataEnricher myKeywordMetadataEnricher;

    // @Bean
    public VectorStore pgVectorStore(@Qualifier("postgresJdbcTemplate") JdbcTemplate jdbcTemplate, EmbeddingModel dashscopeEmbeddingModel) {
        // 创建PgVectorStore实例，配置向量存储的参数
        VectorStore vectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .dimensions(1536)                    // 设置向量的维度，可选，默认为模型维度或1536
                .distanceType(COSINE_DISTANCE)       // 设置计算向量间距离的方法，可选，默认为余弦距离
                .indexType(HNSW)                     // 设置索引类型，可选，默认为HNSW（高效近似最近邻搜索）
                .initializeSchema(true)              // 是否初始化数据库模式，可选，默认为false
                .schemaName("muselink")                // 设置数据库模式名称，可选，默认为"public"
                .vectorTableName("rag_vector")     // 设置存储向量数据的表名，可选，默认为"vector_store"
                .maxDocumentBatchSize(10000)         // 设置文档批量插入的最大数量，可选，默认为10000
                .build();
        // 加载文档
        List<Document> documents = myDocumentReader.readMarkdownFiles();
        // 自主切分文档
        // List<Document> splitDocuments = myTokenSplitter.splitCustomized(documents);
        // 自动补充关键词元信息
        // List<Document> enrichedDocuments = myKeywordMetadataEnricher.enrichDocuments(splitDocuments);
        // 存入向量数据库
        vectorStore.add(documents);
        return vectorStore;
    }

}
