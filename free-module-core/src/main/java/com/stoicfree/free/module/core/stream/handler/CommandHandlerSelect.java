package com.stoicfree.free.module.core.stream.handler;

import java.util.HashSet;
import java.util.Set;

import com.stoicfree.free.module.core.stream.protocol.Command;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2023/3/31 23:11
 */
@Slf4j
public class CommandHandlerSelect {
    private static final Set<CommandHandler> HANDLERS = new HashSet<>();

    static {
        String packageName = CommandHandler.class.getPackage().getName();
        Set<Class<?>> classes = ClassUtil.scanPackageBySuper(packageName, CommandHandler.class);
        for (Class<?> clazz : classes) {
            if (ClassUtil.isAbstract(clazz)) {
                continue;
            }
            try {
                CommandHandler handler = (CommandHandler) ReflectUtil.newInstance(clazz);
                HANDLERS.add(handler);
            } catch (Exception e) {
                log.warn("handler {} can not new instance", clazz.getName());
            }
        }
    }

    /**
     * 选择执行器
     *
     * @param command
     *
     * @return
     */
    public static CommandHandler select(Command command) {
        return HANDLERS.stream()
                .filter(handler -> handler.match(command)).findFirst()
                .orElseThrow(() -> new RuntimeException("command is not support"));
    }
}
