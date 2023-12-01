package com.zqz.dbrouter.dynamic;

import com.zqz.dbrouter.DBContextHolder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @ClassName: DynamicDataSource
 * @author: zqz
 * @date: 2023/11/28 16:49
 *
 * 继承了 AbstractRoutingDataSource 的实现类，这个类里可以存放和读取相应的具体调用的数据源信息
 *
 */

public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return "db" + DBContextHolder.getDbKey();
    }
}
