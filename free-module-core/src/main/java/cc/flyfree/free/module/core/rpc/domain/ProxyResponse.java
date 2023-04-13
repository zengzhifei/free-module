package cc.flyfree.free.module.core.rpc.domain;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/26 02:13
 */
@Data
public class ProxyResponse {
    private boolean success;
    private Object response;
    private String message;

    public ProxyResponse(boolean success, Object response, String message) {
        this.success = success;
        this.response = response;
        this.message = message;
    }

    public static ProxyResponse success(Object response) {
        return new ProxyResponse(true, response, null);
    }

    public static ProxyResponse fail(String message) {
        return new ProxyResponse(false, null, message);
    }
}
