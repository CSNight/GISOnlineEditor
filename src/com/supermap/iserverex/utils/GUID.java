package com.supermap.iserverex.utils;


import java.util.UUID;

public class GUID {
    /**
     * 生成UUID
     *
     * @return
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }

    /**
     * 生成UUID，但会过滤-
     *
     * @return
     */
    public static String getUUID2() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "").toUpperCase();
    }
}
