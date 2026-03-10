package com.gen.config.common;

/**
 * 响应码枚举
 */
public enum ResponseCode {
    // 成功响应码
    SUCCESS("SUCCESS", "操作成功"),

    // 客户端错误响应码
    BAD_REQUEST("BAD_REQUEST", "请求参数错误"),
    VALIDATION_ERROR("VALIDATION_ERROR", "参数验证失败"),
    UNAUTHORIZED("UNAUTHORIZED", "未授权访问"),
    FORBIDDEN("FORBIDDEN", "禁止访问"),
    NOT_FOUND("NOT_FOUND", "资源不存在"),
    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", "请求方法不允许"),
    TOO_MANY_REQUESTS("TOO_MANY_REQUESTS", "请求过于频繁"),

    // 业务错误响应码
    BUSINESS_ERROR("BUSINESS_ERROR", "业务处理失败"),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "无效的凭据"),
    ACCOUNT_LOCKED("ACCOUNT_LOCKED", "账号已被锁定"),
    ACCOUNT_DISABLED("ACCOUNT_DISABLED", "账号已被禁用"),
    INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE", "余额不足"),
    ORDER_NOT_PAYABLE("ORDER_NOT_PAYABLE", "订单不可支付"),

    // 服务端错误响应码
    SYSTEM_ERROR("SYSTEM_ERROR", "系统内部错误"),
    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE", "服务暂时不可用"),
    DATABASE_ERROR("DATABASE_ERROR", "数据库操作失败"),
    NETWORK_ERROR("NETWORK_ERROR", "网络连接失败"),
    TIMEOUT_ERROR("TIMEOUT_ERROR", "请求超时"),

    // 第三方服务错误
    THIRD_PARTY_ERROR("THIRD_PARTY_ERROR", "第三方服务异常");

    private final String code;
    private final String message;

    ResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 根据code获取枚举
     */
    public static ResponseCode fromCode(String code) {
        for (ResponseCode responseCode : values()) {
            if (responseCode.code.equals(code)) {
                return responseCode;
            }
        }
        return null;
    }
}
