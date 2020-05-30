package com.lagou.edu.factory;

import com.lagou.edu.annotation.Autowire;
import com.lagou.edu.annotation.Component;
import com.lagou.edu.utils.TransactionManager;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Proxy;

/**
 * @author 应癫
 * <p>
 * <p>
 * 代理对象工厂：生成代理对象的
 */
@Component
public class ProxyFactory {


    @Autowire
    private TransactionManager transactionManager;



    /*private ProxyFactory(){

    }

    private static ProxyFactory proxyFactory = new ProxyFactory();

    public static ProxyFactory getInstance() {
        return proxyFactory;
    }*/


    /**
     * Jdk动态代理
     *
     * @param originObject 委托对象
     * @return 代理对象
     */
    public Object getJdkProxy(Object originObject) {

        // 获取代理对象
        return Proxy.newProxyInstance(originObject.getClass().getClassLoader(), originObject.getClass().getInterfaces(),
                new ProxyHandler(originObject, transactionManager));

    }


    /**
     * 使用cglib动态代理生成代理对象
     *
     * @param originObject 委托对象
     * @return
     */
    public Object getCglibProxy(Object originObject) {
        return Enhancer.create(originObject.getClass(), new ProxyHandler(originObject, transactionManager));
    }
}
