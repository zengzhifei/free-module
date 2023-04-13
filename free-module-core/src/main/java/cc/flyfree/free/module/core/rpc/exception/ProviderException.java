package cc.flyfree.free.module.core.rpc.exception;

/**
 * @author zengzhifei
 * @date 2023/2/26 00:15
 */
public class ProviderException extends RuntimeException {
    public ProviderException() {
    }

    public ProviderException(String msg) {
        super(msg);
    }
}