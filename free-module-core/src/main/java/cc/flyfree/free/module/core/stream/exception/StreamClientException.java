package cc.flyfree.free.module.core.stream.exception;

/**
 * @author zengzhifei
 * @date 2023/3/29 15:27
 */
public class StreamClientException extends RuntimeException {
    public StreamClientException() {
    }

    public StreamClientException(String msg) {
        super(msg);
    }
}