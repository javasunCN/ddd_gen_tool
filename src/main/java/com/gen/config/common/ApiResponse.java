package com.gen.config.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 统一API响应格式
 * 泛型T表示返回的数据类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS) // 始终包含该属性，无论其值是什么（即使是 null）
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 响应码
     */
    private String code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 错误详情（仅开发环境显示）
     */
    private String errorDetail;

    /**
     * 服务器时间戳（毫秒）
     */
    private Long timestamp;

    /**
     * 请求唯一ID
     */
    private String requestId;

    /**
     * 扩展数据
     */
    private Map<String, Object> extensions;

    /**
     * HTTP状态码
     */
    @JsonIgnore
    private transient HttpStatus httpStatus;

    // ============ 成功响应静态工厂方法 ============

    /**
     * 成功响应（无数据）
     */
    public static <T> ApiResponse<T> success() {
        return ApiResponse.<T>builder()
                .success(true)
                .code(ResponseCode.SUCCESS.getCode())
                .message(ResponseCode.SUCCESS.getMessage())
                .timestamp(Instant.now().toEpochMilli())
                .requestId(generateRequestId())
                .httpStatus(HttpStatus.OK)
                .build();
    }

    /**
     * 成功响应（有数据）
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = success();
        response.setData(data);
        return response;
    }

    /**
     * 成功响应（有数据和自定义消息）
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = success(data);
        response.setMessage(message);
        return response;
    }

    /**
     * 成功响应（有数据、自定义消息和扩展字段）
     */
    public static <T> ApiResponse<T> success(T data, String message, Map<String, Object> extensions) {
        ApiResponse<T> response = success(data, message);
        response.setExtensions(extensions);
        return response;
    }

    // ============ 失败响应静态工厂方法 ============

    /**
     * 失败响应
     */
    public static <T> ApiResponse<T> failure(String code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .timestamp(Instant.now().toEpochMilli())
                .requestId(generateRequestId())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .build();
    }

    /**
     * 失败响应（带错误详情）
     */
    public static <T> ApiResponse<T> failure(String code, String message, String errorDetail) {
        ApiResponse<T> response = failure(code, message);
        response.setErrorDetail(errorDetail);
        return response;
    }

    /**
     * 失败响应（带数据）
     */
    public static <T> ApiResponse<T> failure(String code, String message, T data) {
        ApiResponse<T> response = failure(code, message);
        response.setData(data);
        return response;
    }

    /**
     * 失败响应（从枚举）
     */
    public static <T> ApiResponse<T> failure(ResponseCode responseCode) {
        return failure(responseCode.getCode(), responseCode.getMessage());
    }

    /**
     * 失败响应（从枚举，带详情）
     */
    public static <T> ApiResponse<T> failure(ResponseCode responseCode, String errorDetail) {
        return failure(responseCode.getCode(), responseCode.getMessage(), errorDetail);
    }

    /**
     * 失败响应（从枚举，带HTTP状态码）
     */
    public static <T> ApiResponse<T> failure(ResponseCode responseCode, HttpStatus httpStatus) {
        ApiResponse<T> response = failure(responseCode);
        response.setHttpStatus(httpStatus);
        return response;
    }

    // ============ 便捷方法 ============

    /**
     * 添加扩展字段
     */
    public ApiResponse<T> addExtension(String key, Object value) {
        if (extensions == null) {
            extensions = new HashMap<>();
        }
        extensions.put(key, value);
        return this;
    }

    /**
     * 链式设置消息
     */
    public ApiResponse<T> withMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * 链式设置数据
     */
    public ApiResponse<T> withData(T data) {
        this.data = data;
        return this;
    }

    /**
     * 链式设置HTTP状态码
     */
    public ApiResponse<T> withHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    // ============ 业务特定成功响应 ============

    /**
     * 创建成功响应
     */
    public static <T> ApiResponse<T> created(T data) {
        ApiResponse<T> response = success(data, "创建成功");
        response.setHttpStatus(HttpStatus.CREATED);
        return response;
    }

    /**
     * 更新成功响应
     */
    public static <T> ApiResponse<T> updated(T data) {
        return success(data, "更新成功");
    }

    /**
     * 删除成功响应
     */
    public static <T> ApiResponse<T> deleted() {
        return success(null, "删除成功");
    }

    /**
     * 查询成功响应
     */
    public static <T> ApiResponse<T> fetched(T data) {
        return success(data, "查询成功");
    }

    // ============ 分页响应 ============

    /**
     * 分页响应
     */
    public static <T> ApiResponse<PageData<T>> paged(PageData<T> pageData) {
        return success(pageData, "查询成功");
    }

    /**
     * 分页响应（自定义消息）
     */
    public static <T> ApiResponse<PageData<T>> paged(PageData<T> pageData, String message) {
        return success(pageData, message);
    }

    // ============ 异常响应 ============

    /**
     * 参数验证失败响应
     */
    public static <T> ApiResponse<T> validationFailed(String message) {
        return failure(ResponseCode.VALIDATION_ERROR.getCode(), message);
    }

    /**
     * 未授权响应
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        ApiResponse<T> response = failure(ResponseCode.UNAUTHORIZED.getCode(), message);
        response.setHttpStatus(HttpStatus.UNAUTHORIZED);
        return response;
    }

    /**
     * 禁止访问响应
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        ApiResponse<T> response = failure(ResponseCode.FORBIDDEN.getCode(), message);
        response.setHttpStatus(HttpStatus.FORBIDDEN);
        return response;
    }

    /**
     * 资源未找到响应
     */
    public static <T> ApiResponse<T> notFound(String message) {
        ApiResponse<T> response = failure(ResponseCode.NOT_FOUND.getCode(), message);
        response.setHttpStatus(HttpStatus.NOT_FOUND);
        return response;
    }

    /**
     * 系统异常响应
     */
    public static <T> ApiResponse<T> systemError(String message) {
        ApiResponse<T> response = failure(ResponseCode.SYSTEM_ERROR.getCode(), message);
        response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return response;
    }

    /**
     * 系统异常响应（带详情）
     */
    public static <T> ApiResponse<T> systemError(String message, String errorDetail) {
        ApiResponse<T> response = systemError(message);
        response.setErrorDetail(errorDetail);
        return response;
    }

    /**
     * 请求过多响应
     */
    public static <T> ApiResponse<T> tooManyRequests(String message) {
        ApiResponse<T> response = failure(ResponseCode.TOO_MANY_REQUESTS.getCode(), message);
        response.setHttpStatus(HttpStatus.TOO_MANY_REQUESTS);
        return response;
    }

    /**
     * 业务异常响应
     */
    public static <T> ApiResponse<T> businessError(String code, String message) {
        return failure(code, message);
    }

    // ============ 请求追踪相关 ============

    /**
     * 设置请求ID
     */
    public ApiResponse<T> withRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    /**
     * 设置服务器时间戳
     */
    public ApiResponse<T> withTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    /**
     * 设置当前请求上下文
     */
    public ApiResponse<T> withRequestContext(String requestId) {
        this.requestId = requestId;
        this.timestamp = Instant.now().toEpochMilli();
        return this;
    }

    // ============ 工具方法 ============

    /**
     * 生成请求ID
     */
    private static String generateRequestId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 判断是否成功
     */
    public boolean isOk() {
        return success;
    }

    /**
     * 判断是否失败
     */
    public boolean isError() {
        return !success;
    }

    /**
     * 获取HTTP状态码，默认为200
     */
    public HttpStatus getHttpStatusOrDefault() {
        return httpStatus != null ? httpStatus : HttpStatus.OK;
    }

    /**
     * 转换为字符串（用于日志）
     */
    public String toLogString() {
        return String.format("ApiResponse{success=%s, code='%s', message='%s', requestId='%s'}",
                success, code, message, requestId);
    }


}
