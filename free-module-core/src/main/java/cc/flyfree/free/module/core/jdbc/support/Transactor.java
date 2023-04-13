package cc.flyfree.free.module.core.jdbc.support;

import java.util.function.Consumer;

import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2022/8/17 10:44
 */
@Slf4j
public class Transactor {
    public static void registerAfterCompletion(Consumer<Integer> consumer) {
        // 不存在事务
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            log.info("[Transactor] registerAfterCompletion not transaction");
            consumer.accept(null);
        } else {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronizationAdapter() {
                        @Override
                        public void afterCompletion(int status) {
                            log.info("[Transactor] registerAfterCompletion status = {}", status);
                            consumer.accept(status);
                        }
                    }
            );
        }
    }
}
