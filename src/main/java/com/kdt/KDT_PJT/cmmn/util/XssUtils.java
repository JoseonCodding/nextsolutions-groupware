package com.kdt.KDT_PJT.cmmn.util;

/**
 * XSS(Cross-Site Scripting) 방어 유틸리티.
 * HTML 특수문자를 엔티티로 이스케이프한다.
 */
public final class XssUtils {

    private XssUtils() {}

    public static String escape(String input) {
        if (input == null) return null;
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#039;");
    }
}
