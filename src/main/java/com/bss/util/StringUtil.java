package com.bss.util;

public class StringUtil {
    /**
     * connect strings by StringBuilder
     * @param strings
     * @return result of connect strings
     */
    private String con(String... ss) {
        StringBuilder result = new StringBuilder();
        for (String s : ss) {
            result.append(s);
        }
        return result.toString();

    }
}