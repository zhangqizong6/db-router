package com.zqz.dbrouter;

import com.zqz.dbrouter.annotation.DBRouter;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * @ClassName: DBRouterJoinPoint
 * @author: zqz
 * @date: 2023/11/29 10:01
 * @Aspect 作用是把当前类标识为一个切面供容器读取
 * @Pointcut Pointcut是植入Advice的触发条件
 * @Around 环绕增强，相当于MethodInterceptor
 */

@Aspect
@Component("db-router-point")
public class DBRouterJoinPoint {

    //日志
    private Logger logger = LoggerFactory.getLogger(DBRouterJoinPoint.class);

    //这个在一开始的config配置类注入过的 引用即可
    @Autowired
    private DBRouterConfig dbRouterConfig;

    //带有@DBRouter注解的话作为注入条件
    @Pointcut("@annotation(com.zqz.dbrouter.annotation.DBRouter)")
    private void aopPoint() {
    }

    //Proceedingjoinpoint 继承了JoinPoint，在JoinPoint的基础上暴露出 proceed()， 这个方法是AOP代理链执行的方法
    @Around("aopPoint() && @annotation(dbRouter)")
    private Object doPoint(ProceedingJoinPoint jp, DBRouter dbRouter) throws Throwable {

        String dbKey = dbRouter.key();
        if (StringUtils.isBlank(dbKey)) throw new RuntimeException("annotation DBRouter key is null");

        //计算路由
        String dbKeyAttr = getAttrValue(dbKey, jp.getArgs());
        int size = dbRouterConfig.getDbCount() * dbRouterConfig.getTbCount();

        /**
         * 扰动函数
         * HashMap 一样的扰动函数逻辑，让数据分散的更加散列
         *
         */
        int idx = (size - 1) & (dbKeyAttr.hashCode() ^ (dbKeyAttr.hashCode() >>> 16));

        //库表索引
        int dbIdx = idx / dbRouterConfig.getTbCount() + 1;
        int tbIdx = idx - dbRouterConfig.getTbCount() * (dbIdx - 1);

        //设置到ThreadLocal
        DBContextHolder.setDbKey(String.format("%02d", dbIdx));
        DBContextHolder.setTbKey(String.format("%02d", tbIdx));
        logger.info("数据库路由 method：{} dbIdx：{} tbIdx：{}", getMethod(jp).getName(), dbIdx, tbIdx);

        //返回结果
        try {
            return jp.proceed();
        } finally {
            DBContextHolder.clearDbKey();
            DBContextHolder.clearTbKey();
        }

    }

    private Method getMethod(ProceedingJoinPoint jp) throws NoSuchMethodException {
        Signature signature = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        return jp.getTarget().getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
    }

    public String getAttrValue(String attr, Object[] args) {
        String filedValue = null;
        //PropertyUtils就会原色的保留Bean原来的类型。
        for (Object arg : args) {
            if (StringUtils.isNotBlank(filedValue)) break;
            try {
                filedValue = BeanUtils.getProperty(arg, attr);
            } catch (Exception e) {
                logger.error("获取路由属性值失败 attr：{}", attr, e);
            }
        }
        return filedValue;
    }

}
