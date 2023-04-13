package cc.flyfree.free.module.core.rpc.exception;

/**
 * @author zengzhifei
 * @date 2023/2/26 00:15
 */
public class ConsumerException extends RuntimeException {
    public ConsumerException() {
    }

    public ConsumerException(String msg) {
        super(msg);
    }
}