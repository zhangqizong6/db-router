package com.zqz.dbrouter;

/**
 * @ClassName: DBContextHolder
 * @author: zqz
 * @date: 2023/11/28 16:52
 */

public class DBContextHolder {

    private static final ThreadLocal<String> dbKey = new ThreadLocal<>();
    private static final ThreadLocal<String> tbKey = new ThreadLocal<>();

    public static void setDbKey(String dbKeyIndex) {
        dbKey.set(dbKeyIndex);
    }

    public static String getDbKey() {
        return dbKey.get();
    }

    public static void setTbKey(String tbKeyIndex) {
        tbKey.set(tbKeyIndex);
    }

    public static String getTbKey() {
        return tbKey.get();
    }

    public static void clearDbKey() {
        dbKey.remove();
    }

    public static void clearTbKey() {
        tbKey.remove();
    }


}
