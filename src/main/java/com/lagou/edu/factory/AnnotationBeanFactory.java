package com.lagou.edu.factory;

import com.lagou.edu.annotation.Component;
import com.lagou.edu.annotation.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AnnotationBeanFactory {

    private static Map<String, Object> beansMap;  // 存储对象


    static {
        String basePackage = "com.lagou.edu";
        try {

            //扫描包下所有类型
            List<String> qualifiedClassNameList = new ClasspathScanner(basePackage).getFullyQualifiedClassNameList();

            AnnotationResolver annotationResolver = new AnnotationResolver(qualifiedClassNameList);
            annotationResolver.createBeanForAnnotation(Component.class);
            annotationResolver.createBeanForAnnotation(Service.class);
            beansMap = annotationResolver.getBeansMap();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    // 任务二：对外提供获取实例对象的接口（根据id获取）
    public static Object getBean(String id) {
        return beansMap.get(id);
    }

    public static <T> T getBean(Class<T> beanClass) {
        return (T) getBean(beanClass.getName());
    }
}
