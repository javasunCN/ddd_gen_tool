package com.gen.st4;


/**
 * 模板校验异常
 */
public class TemplateValidationException extends RuntimeException {

    public TemplateValidationException(String message) {
        super(message);
    }

    public TemplateValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
