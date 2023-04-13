package cc.flyfree.free.module.core.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2022/12/9 17:57
 */
@Slf4j
public class Md5Utils {
    public static String md5File(String fileUrl) {
        URL url = URLUtil.url(fileUrl);
        try (InputStream inputStream = URLUtil.getStream(url)) {
            return DigestUtil.md5Hex(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

