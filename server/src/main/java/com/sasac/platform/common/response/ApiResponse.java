package com.sasac.platform.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Unified API response wrapper.
 * <p>
 * All REST controllers return this type, ensuring a consistent
 * JSON envelope across the entire application.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String error;
    private PageMeta meta;

    // ---- static factories ----

    /** Successful response with data. */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    /** Successful response with data and pagination meta. */
    public static <T> ApiResponse<T> success(T data, PageMeta meta) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .meta(meta)
                .build();
    }

    /** Error response with a human-readable message. */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(message)
                .build();
    }

    // ---- inner types ----

    /** Pagination metadata. */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageMeta {
        private long total;
        private int page;
        private int limit;
    }
}
