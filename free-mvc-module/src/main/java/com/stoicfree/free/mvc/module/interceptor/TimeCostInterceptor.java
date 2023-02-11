/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.stoicfree.free.mvc.module.interceptor;

import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2019/9/7 17:31
 */
@Slf4j
public class TimeCostInterceptor implements HandlerInterceptor {
    /**
     * 最大打印值长度
     */
    private static final int LOG_MAX_VALUE_LENGTH = 256;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        TimeCost.cleanUp();
        TimeCost timeCost = TimeCost.getTimeCost();
        timeCost.setStart(System.currentTimeMillis());
        timeCost.setPath(request.getRequestURI());

        Enumeration<String> enu = request.getParameterNames();
        Map<String, Object> param = Maps.newConcurrentMap();
        while (enu.hasMoreElements()) {
            String paraName = enu.nextElement();
            String parameterValue = request.getParameter(paraName);
            if (Objects.nonNull(parameterValue) && parameterValue.length() > LOG_MAX_VALUE_LENGTH) {
                param.put(paraName, "param value too long");
            } else {
                param.put(paraName, parameterValue);
            }
        }

        // 文件参数
        if (request instanceof MultipartRequest) {
            Map<String, MultipartFile> fileMap = ((MultipartRequest) request).getFileMap();
            param.putAll(fileMap);
        }

        String referer = request.getHeader("referer");
        if (StringUtils.isNotEmpty(referer)) {
            timeCost.setRef(referer);
        } else {
            timeCost.setRef("");
        }

        timeCost.setParam(param);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) {
        TimeCost timeCost = TimeCost.getTimeCost();
        timeCost.setEnd(System.currentTimeMillis());
        log.info(timeCost.toString());
        TimeCost.cleanUp();
    }

    @Data
    private static class TimeCost {
        public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
        private static final ThreadLocal<TimeCost> CURRENT_TIME_COST = ThreadLocal.withInitial(TimeCost::new);
        private Long start;
        private Long end;
        private String path;
        private String ref;
        private Map<String, Object> param;

        public static TimeCost getTimeCost() {
            return CURRENT_TIME_COST.get();
        }

        public static void cleanUp() {
            CURRENT_TIME_COST.remove();
        }

        public Long getCost() {
            if (end == null || start == null) {
                return 0L;
            }
            return end - start;
        }

        @Override
        public String toString() {
            return "TimeCost{" +
                    "path='" + path + '\'' +
                    ", start=" + DateFormatUtils.format(new Date(start), DEFAULT_PATTERN) +
                    ", end=" + DateFormatUtils.format(new Date(end), DEFAULT_PATTERN) +
                    ", cost=" + getCost() +
                    ", param=" + param +
                    ", ref=" + ref +
                    '}';

        }
    }
}
