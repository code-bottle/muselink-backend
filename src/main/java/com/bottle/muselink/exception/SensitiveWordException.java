package com.bottle.muselink.exception;

public class SensitiveWordException extends RuntimeException{
    public SensitiveWordException() {
        super("用户输入包含敏感内容");
    }
    public SensitiveWordException(String message) {
        super(message);
    }
}
