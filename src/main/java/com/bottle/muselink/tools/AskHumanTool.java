package com.bottle.muselink.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

@Component
public class AskHumanTool {

    private final Scanner scanner = new Scanner(System.in);

    @Tool(description = "Ask the human user for input when additional information is required to proceed with the task.")
    public CompletableFuture<String> askHuman(@ToolParam(description = "The question to ask the human user") String question) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.print("[AI 需要用户补充输入信息] " + question + ": ");
            return scanner.nextLine();  // 阻塞等待用户输入
        });
    }
}

