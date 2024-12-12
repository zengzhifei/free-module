package cc.flyfree.free.module.core.common.support;

/**
 * @author zengzhifei
 * @date 2024/12/12 17:36
 */
public class SystemPatcher {
    public static void patchHttpUrlConnection() {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
    }

    public static void patchLog4j2() {
        System.setProperty("log4j2.AsyncQueueFullPolicy", "Discard");
    }
}
