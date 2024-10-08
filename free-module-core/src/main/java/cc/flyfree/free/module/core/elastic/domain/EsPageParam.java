package cc.flyfree.free.module.core.elastic.domain;

import java.util.List;

import org.springframework.beans.BeanUtils;

import cc.flyfree.free.module.core.common.domain.PageParam;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2022/8/12 15:50
 */
@Data
public class EsPageParam {
    private int pageNo = 1;
    private int pageSize = 50;
    private List<String> orderByAsc;
    private List<String> orderByDesc;

    public static EsPageParam convert(PageParam pageParam) {
        EsPageParam esPageParam = new EsPageParam();
        BeanUtils.copyProperties(pageParam, esPageParam);
        return esPageParam;
    }
}
