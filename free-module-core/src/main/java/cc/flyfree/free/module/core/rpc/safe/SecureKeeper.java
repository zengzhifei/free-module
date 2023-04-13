package cc.flyfree.free.module.core.rpc.safe;

import java.nio.charset.StandardCharsets;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;

/**
 * @author zengzhifei
 * @date 2023/2/26 10:25
 */
public class SecureKeeper {
    private static final AES AES = SecureUtil.aes("8hGYB)MlI4wkf{j0".getBytes(StandardCharsets.UTF_8));

    public static String getProxyToken(String productId) {
        return AES.encryptHex(productId);
    }

    public static String getProductId(String proxyToken) {
        return AES.decryptStr(proxyToken);
    }
}

