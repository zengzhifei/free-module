package cc.flyfree.free.module.core.common.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zengzhifei
 * @date 2022/8/11 14:49
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Paging<T> {
    private long count;
    private List<T> rows = new ArrayList<>();
}
