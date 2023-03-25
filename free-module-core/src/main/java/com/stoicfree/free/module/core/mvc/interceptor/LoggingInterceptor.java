package com.stoicfree.free.module.core.mvc.interceptor;

import java.util.Map;
import java.util.StringJoiner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.HandlerInterceptor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.stoicfree.free.module.core.common.support.Safes;
import com.stoicfree.free.module.core.common.util.DateUtils;
import com.stoicfree.free.module.core.mvc.config.LoggingProperties;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DatePattern;
import cn.hutool.extra.servlet.ServletUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 请求记录
 *
 * @author zengzhifei
 * @date 2023/3/24 16:09
 */
@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {
    private final LoggingProperties properties;

    public LoggingInterceptor(LoggingProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Logging logging = Logging.removeAndCreate();

        logging.setPath(request.getRequestURI());
        logging.setIp(ServletUtil.getClientIP(request));
        logging.setStart(System.currentTimeMillis());
        logging.setMethod(request.getMethod());
        Map<String, String> headerMap = Safes.of(ServletUtil.getHeaderMap(request));
        headerMap.remove("cookie");
        logging.setHeader(headerMap);
        logging.setParam(ServletUtil.getParamMap(request));
        logging.setBody(ServletUtil.getBody(request));
        if (ServletUtil.isMultipart(request) && request instanceof MultipartRequest) {
            logging.setFile(((MultipartRequest) request).getFileMap());
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) {
        Logging logging = Logging.get();

        logging.setEnd(System.currentTimeMillis());
        log.info(properties.isToPrettyString() ? logging.toPrettyString() : logging.toString());

        Logging.remove();
    }

    @Data
    private static class Logging {
        private static final ThreadLocal<Logging> LOGGING = ThreadLocal.withInitial(Logging::new);
        private static final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        private String path;
        private String ip;
        private long start;
        private long end;
        private long cost;
        private String method;
        private Map<String, String> header;
        private Map<String, String> param;
        private Map<String, MultipartFile> file;
        private String body;

        public static Logging get() {
            return LOGGING.get();
        }

        public static void remove() {
            LOGGING.remove();
        }

        public static Logging removeAndCreate() {
            remove();
            return get();
        }

        @Override
        public String toString() {
            StringJoiner joiner = new StringJoiner(" ");
            joiner.add("Logging{");
            joiner.add(String.format("path[%s]", path));
            joiner.add(String.format("ip[%s]", ip));
            joiner.add(String.format("start[%s]", DateUtils.format(start, DatePattern.NORM_DATETIME_MS_PATTERN)));
            joiner.add(String.format("end[%s]", DateUtils.format(end, DatePattern.NORM_DATETIME_MS_PATTERN)));
            joiner.add(String.format("cost[%s]", end - start));
            joiner.add(String.format("method[%s]", method));
            joiner.add(String.format("header[%s]", header != null ? header : ""));
            joiner.add(String.format("param[%s]", param != null ? param : ""));
            joiner.add(String.format("file[%s]", file != null ? file : ""));
            joiner.add(String.format("body[%s]", body != null ? body : ""));
            joiner.add("}");

            return joiner.toString();
        }

        public String toPrettyString() {
            JsonObject object = gson.toJsonTree(this).getAsJsonObject();
            object.addProperty("start", DateUtils.format(start, DatePattern.NORM_DATETIME_MS_PATTERN));
            object.addProperty("end", DateUtils.format(end, DatePattern.NORM_DATETIME_MS_PATTERN));
            object.addProperty("cost", DateUtils.formatBetween(end - start, BetweenFormatter.Level.MILLISECOND));
            object.add("body", gson.fromJson(body, JsonObject.class));

            return String.format("Logging:\n%s", gson.toJson(object));
        }
    }
}
