package com.zqz.dbrouter;

/**
 * @ClassName: DBRouterConfig
 * @author: zqz
 * @date: 2023/11/28 15:59
 */

public class DBRouterConfig {
    private int dbCount; //分库数

    private int tbCount; //分表数

    public int getDbCount() {
        return dbCount;
    }

    public void setDbCount(int dbCount) {
        this.dbCount = dbCount;
    }

    public int getTbCount() {
        return tbCount;
    }

    public void setTbCount(int tbCount) {
        this.tbCount = tbCount;
    }

    public DBRouterConfig() {
    }

    public DBRouterConfig(int dbCount, int tbCount) {
        this.dbCount = dbCount;
        this.tbCount = tbCount;
    }


}
