package cc.flyfree.free.module.core.mvc.config;

import java.util.List;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2024/12/23 19:17
 */
@Data
public class UploadProperties {
    private List<Bucket> buckets;

    @Data
    public static class Bucket {
        private String bucket;
        private String directory;
        private String allowExt;
    }
}
