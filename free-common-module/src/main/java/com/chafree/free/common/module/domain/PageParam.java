package com.chafree.free.common.module.domain;

import java.util.List;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2022/8/11 14:44
 */
@Data
public class PageParam {
    public static final Integer DEFAULT_PAGE_NO = 1;
    public static final Integer DEFAULT_PAGE_SIZE = 50;

    private Integer pageNo = 1;
    private Integer pageSize = 50;
    private List<String> orderByAsc;
    private List<String> orderByDesc;

    public static PageParam correct(PageParam pageParam) {
        if (pageParam == null) {
            pageParam = new PageParam();
            pageParam.setPageNo(DEFAULT_PAGE_NO);
            pageParam.setPageSize(DEFAULT_PAGE_SIZE);
            return pageParam;
        }
        if (pageParam.getPageNo() <= 0) {
            pageParam.setPageNo(DEFAULT_PAGE_NO);
        }
        if (pageParam.getPageSize() <= 0 || pageParam.getPageSize() > DEFAULT_PAGE_SIZE) {
            pageParam.setPageSize(DEFAULT_PAGE_SIZE);
        }
        return pageParam;
    }
}
