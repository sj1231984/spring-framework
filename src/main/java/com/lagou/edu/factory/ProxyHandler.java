package com.lagou.edu.factory;

import com.lagou.edu.utils.TransactionManager;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ProxyHandler implements MethodInterceptor, InvocationHandler {

    private Object originObject;
    private TransactionManager transactionManager;

    ProxyHandler(Object originObject) {
        this.originObject = originObject;
    }

    public ProxyHandler(Object originObject, TransactionManager transactionManager) {
        this.originObject = originObject;
        this.transactionManager = transactionManager;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return invoke(o, method, objects);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;

        try {
            // 开启事务(关闭事务的自动提交)
            transactionManager.beginTransaction();

            result = method.invoke(originObject, args);

            // 提交事务

            transactionManager.commit();
        } catch (Exception e) {
            e.printStackTrace();
            // 回滚事务
            transactionManager.rollback();

            // 抛出异常便于上层servlet捕获
            throw e;

        }

        return result;
    }
}
