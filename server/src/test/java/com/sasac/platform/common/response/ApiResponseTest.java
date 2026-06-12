package com.sasac.platform.common.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    void success_shouldHaveTrueStatusAndData() {
        ApiResponse<String> response = ApiResponse.success("hello");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo("hello");
        assertThat(response.getError()).isNull();
        assertThat(response.getMeta()).isNull();
    }

    @Test
    void error_shouldHaveFalseStatusAndErrorMessage() {
        ApiResponse<String> response = ApiResponse.error("something went wrong");

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).isEqualTo("something went wrong");
        assertThat(response.getData()).isNull();
        assertThat(response.getMeta()).isNull();
    }

    @Test
    void paged_shouldIncludeMeta() {
        ApiResponse.PageMeta meta = new ApiResponse.PageMeta(100L, 1, 10);
        ApiResponse<String> response = ApiResponse.success("data", meta);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo("data");
        assertThat(response.getMeta()).isNotNull();
        assertThat(response.getMeta().getTotal()).isEqualTo(100L);
        assertThat(response.getMeta().getPage()).isEqualTo(1);
        assertThat(response.getMeta().getLimit()).isEqualTo(10);
    }
}
