package cc.flyfree.free.module.core.elastic.domain;

import java.util.LinkedHashMap;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2022/8/12 15:43
 */
@Data
public class EsPage<T> {
    /**
     * 总条数
     */
    private long total;
    /**
     * 当前页
     */
    private int pageNo;
    /**
     * 分页大小
     */
    private int pageSize;
    /**
     * 最大分页数
     */
    private int pageCount;
    /**
     * 数据列表
     */
    private LinkedHashMap<String, T> items = new LinkedHashMap<>();

    public EsPage<T> convert(EsPageParam esPageParam) {
        this.pageNo = esPageParam.getPageNo();
        this.pageSize = esPageParam.getPageSize();
        this.pageCount = (int) Math.ceil((double) this.total / esPageParam.getPageSize());
        return this;
    }
}
