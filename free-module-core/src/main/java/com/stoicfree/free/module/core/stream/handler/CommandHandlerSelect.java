package com.stoicfree.free.module.core.stream.handler;

import java.util.HashSet;
import java.util.Set;

import com.stoicfree.free.module.core.stream.enums.Command;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;

/**
 * @author zengzhifei
 * @date 2023/3/31 23:11
 */
public class CommandHandlerSelect {
    private static final Set<CommandHandler> HANDLERS = new HashSet<>();

    static {
        Set<Class<?>> classes = ClassUtil.scanPackageBySuper(CommandHandler.class.getPackage().getName(),
                CommandHandler.class);
        for (Class<?> clazz : classes) {
            CommandHandler handler = (CommandHandler) ReflectUtil.newInstance(clazz);
            HANDLERS.add(handler);
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
        return HANDLERS.stream().filter(handler -> handler.match(command)).findFirst()
                .orElseThrow(() -> new RuntimeException("command is not support"));
    }
}
