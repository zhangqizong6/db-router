package com.zqz.dbrouter.config;

import com.zqz.dbrouter.DBRouterConfig;
import com.zqz.dbrouter.dynamic.DynamicDataSource;
import com.zqz.dbrouter.util.PropertyUtil;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: DataSourceAutoConfig
 * @author: zqz
 * @date: 2023/11/28 15:13
 * <p>
 * EnvironmentAware：
 * 凡注册到Spring容器内的bean，实现了EnvironmentAware接口重写setEnvironment方法后，
 * 在工程启动时可以获得application.properties的配置文件配置的属性值
 * @Configuration 用 @Configuration 标记类作为配置类替换 xml 配置文件
 */
@Configuration
public class DataSourceAutoConfig implements EnvironmentAware {

    private Map<String, Map<String, Object>> dataSourceMap = new HashMap<>();

    private Integer dbCount; //分库数
    private Integer tbCount; //分表数

    //注入spring容器
    @Bean
    public DBRouterConfig dbRouterConfig() {
        return new DBRouterConfig(dbCount, tbCount);
    }

    @Bean
    public DataSource dataSource() {
        //创建数据源
        Map<Object, Object> targetDataSources = new HashMap<>();
        for (String dataInfo : dataSourceMap.keySet()) {
            Map<String, Object> objMap = dataSourceMap.get(dataInfo);
            /**
             * DriverManagerDataSource
             * jdbc驱动管理器类
             */
            targetDataSources.put(dataInfo, new DriverManagerDataSource(
                    objMap.get("url").toString(),
                    objMap.get("username").toString(),
                    objMap.get("password").toString()));
        }
        /**
         * 设置数据源
         *
         * 提供一个 DataSource 的实例化对象，
         * 这个对象我们就放在 DataSourceAutoConfig 来实现，并且这里提供的数据源是可以动态变换的，也就是支持动态切换数据源。
         */
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSources);
        return dynamicDataSource;

    }

    /**
     * 数据源配置读取
     *
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {

        //prefix，是数据源配置的开头信息，你可以自定义需要的开头内容。
        String prefix = "router.jdbc.datasource.";

        Integer dbCount = Integer.valueOf(environment.getProperty(prefix + "dbCount"));
        Integer tbCount = Integer.valueOf(environment.getProperty(prefix + "tbCount"));

        String dataSources = environment.getProperty(prefix + "list");
        for (String dbInfo : dataSources.split(",")) {
            //通过工具类来选定配置文件下的配置map
            Map<String, Object> dataSourceProps = PropertyUtil.handle(environment, prefix + dbInfo, Map.class);
            dataSourceMap.put(dbInfo, dataSourceProps);
        }


    }
}
