package com.stoicfree.free.common.module.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

/**
 * UrlUtils
 *
 * @author zengzhifei
 * @date 2019/12/1 21:15
 */
@Slf4j
public class UrlUtils {
    private static final String RE_TOP_DOMAIN = "(com\\.cn|net\\.cn|gov\\.cn|org\\.nz|org\\" +
            ".cn|com|net|org|gov|cc|biz|info|cn|co|me)";

    private static final String RE_TOP_1 = "(\\w*\\.?){1}\\." + RE_TOP_DOMAIN;

    private static final String RE_TOP_2 = "(\\w*\\.?){2}\\." + RE_TOP_DOMAIN;

    private static final String RE_TOP_3 = "(\\w*\\.?){3}\\." + RE_TOP_DOMAIN;

    private static final Pattern PATTEN_IP = Pattern.compile("((http://)|(https://))?((\\d+\\.){3}(\\d+))");
    private static final Pattern PATTEN_TOP1 = Pattern.compile(RE_TOP_1);
    private static final Pattern PATTEN_TOP2 = Pattern.compile(RE_TOP_2);
    private static final Pattern PATTEN_TOP3 = Pattern.compile(RE_TOP_3);

    public static String getDomain(String url) {
        try {
            URL var = new URL(url);
            return var.getHost();
        } catch (MalformedURLException e) {
            log.error("", e);
            return "";
        }
    }

    public static String getDomain(String url, int level) {
        Matcher matcher = PATTEN_IP.matcher(url);
        if (matcher.find()) {
            return matcher.group(4);
        }

        switch (level) {
            case 1:
                matcher = PATTEN_TOP1.matcher(url);
                break;
            case 2:
                matcher = PATTEN_TOP2.matcher(url);
                break;
            case 3:
                matcher = PATTEN_TOP3.matcher(url);
                break;
            default:
                return "";
        }
        if (matcher.find()) {
            return matcher.group(0);
        }
        return "localhost";
    }
}