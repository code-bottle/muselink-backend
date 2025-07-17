package com.bottle.muselink.tools;

import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolRegisterConfig {
    @Value("${websearch.api-key}")
    private String webSearchApiKey;
    /**
     * 集中注册和管理工具
     * @return
     */
    @Bean
    public ToolCallback[] allTools(){
        return ToolCallbacks.from(
                new FileOperationTool(),
                new WebSearchTool(webSearchApiKey),
                new WebCrawlerTool(),
                new ResourceDownloadTool(),
                new TerminalOperationTool(),
                new PdfGenerationTool()
        );
    }
}
