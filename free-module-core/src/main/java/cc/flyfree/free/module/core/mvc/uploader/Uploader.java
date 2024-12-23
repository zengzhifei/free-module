package cc.flyfree.free.module.core.mvc.uploader;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import cc.flyfree.free.module.core.common.enums.ErrorCode;
import cc.flyfree.free.module.core.common.exception.BizException;
import cc.flyfree.free.module.core.common.support.Assert;
import cc.flyfree.free.module.core.common.support.Safes;
import cc.flyfree.free.module.core.mvc.config.UploadProperties;
import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.DigestUtil;

/**
 * @author zengzhifei
 * @date 2024/12/23 11:43
 */
public class Uploader {
    private final UploadProperties properties;

    public Uploader(UploadProperties properties) {
        this.properties = properties;
    }

    public String upload(String bucket, MultipartFile file) {
        Assert.notBlank(bucket, ErrorCode.EMPTY_PARAMS);
        Assert.notNull(file, ErrorCode.EMPTY_PARAMS);
        Assert.isFalse(file.isEmpty(), ErrorCode.EMPTY_PARAMS);

        Optional<UploadProperties.Bucket> optional = Safes.of(properties.getBuckets()).stream()
                .filter(e -> e.getBucket().equals(bucket))
                .findFirst();
        if (optional.isEmpty()) {
            throw new BizException(ErrorCode.EMPTY_PARAMS, "upload file is invalid");
        }
        UploadProperties.Bucket property = optional.get();

        String fileExt = Safes.of(FilenameUtils.getExtension(file.getOriginalFilename())).toLowerCase();
        List<String> allowExt = Arrays.stream(property.getAllowExt().split(",")).toList();
        if (!allowExt.contains(fileExt)) {
            throw new BizException(ErrorCode.EMPTY_PARAMS, "upload file extension is not allowed");
        }

        try {
            String md5 = DigestUtil.md5Hex(file.getBytes());
            String path = property.getDirectory() + "/" + bucket;
            FileUtil.mkdir(path);
            String target = path + "/" + md5 + "." + fileExt;
            File targetFile = new File(target);
            file.transferTo(targetFile);

            return bucket + "/" + targetFile.getName();
        } catch (IOException e) {
            throw new BizException(ErrorCode.IO_EXCEPTION);
        }
    }
}
