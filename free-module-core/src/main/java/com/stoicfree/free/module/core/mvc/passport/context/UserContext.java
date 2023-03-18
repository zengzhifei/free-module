package com.stoicfree.free.module.core.mvc.passport.context;

import java.util.Optional;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zengzhifei
 * @date 2023/2/18 10:50
 */
@Data
public class UserContext {
    private static final ThreadLocal<User> USER_THREAD_LOCAL = new ThreadLocal<>();

    public static User get() {
        return Optional.ofNullable(USER_THREAD_LOCAL.get()).orElse(new User());
    }

    public static void set(User user) {
        USER_THREAD_LOCAL.set(user);
    }

    public static void remove() {
        USER_THREAD_LOCAL.remove();
    }

    public static String getUuid() {
        return get().getUuid();
    }

    public static String getUsername() {
        return get().getUsername();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private String uuid;
        private String username;
        private Set<String> userRoles;
    }
}
