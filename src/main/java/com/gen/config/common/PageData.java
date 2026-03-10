package com.gen.config.common;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页数据封装
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageData<T> {
    /**
     * 数据列表
     */
    private java.util.List<T> list;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer size;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 是否有下一页
     */
    private Boolean hasNext;

    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;

    public static <T> PageData<T> of(java.util.List<T> list, Integer page, Integer size, Long total) {
        Integer pages = (int) Math.ceil((double) total / size);
        Boolean hasNext = page < pages;
        Boolean hasPrevious = page > 1;

        return PageData.<T>builder()
                .list(list)
                .page(page)
                .size(size)
                .total(total)
                .pages(pages)
                .hasNext(hasNext)
                .hasPrevious(hasPrevious)
                .build();
    }
}
