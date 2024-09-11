package cc.flyfree.free.module.core.common.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;

/**
 * 签名算法
 *
 * @author zengzhifei
 * @date 2022/8/24 10:16
 */
@Slf4j
public class SignatureUtils {
    public static String hmacSha1Sign(Map<String, String> params, String secretKey) {
        return sign(params, "HmacSHA1", secretKey);
    }

    private static String sign(Map<String, String> params, String method, String secretKey) {
        try {
            String signString = getSignString(params);
            Mac mac = Mac.getInstance(method);
            SecretKeySpec secretKeySpec =
                    new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), mac.getAlgorithm());
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(signString.getBytes(StandardCharsets.UTF_8));
            String signature = DatatypeConverter.printBase64Binary(hash);
            return URLEncoder.encode(signature, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            log.error("sign[{}] fail", method, e);
            return null;
        }
    }

    private static String getSignString(Map<String, String> params) {
        TreeMap<String, Object> map = new TreeMap<>(params);
        StringJoiner stringJoiner = new StringJoiner("&");
        for (String key : map.keySet()) {
            stringJoiner.add(key + "=" + params.get(key));
        }
        return stringJoiner.toString();
    }
}
