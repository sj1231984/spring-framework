package com.lagou.edu.factory;

import com.lagou.edu.annotation.Autowire;
import com.lagou.edu.annotation.Transactional;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class AnnotationResolver {

    private final List<String> qualifiedClassNameList;

    private Map<String, Object> beansMap = new HashMap<>();  // 存储对象

    private Map<Class, Object> beanClassAndOriginObjectCache = new HashMap<>();

    public AnnotationResolver(List<String> qualifiedClassNameList) {
        this.qualifiedClassNameList = qualifiedClassNameList;
    }

    public List<Class> resolveClassWithAnnotation(Class<? extends Annotation> annotationClass) throws ClassNotFoundException {
        List<Class> classForAnnotation = new ArrayList<>();
        for (String classPath : qualifiedClassNameList) {
            Class<?> classObj = Class.forName(classPath);
            Annotation annotation = classObj.getAnnotation(annotationClass);
            if (Objects.nonNull(annotation)) {
                classForAnnotation.add(classObj);
            }
        }
        return classForAnnotation;
    }


    public void createBeanForAnnotation(Class<? extends Annotation> annotation) throws Exception {
        List<Class> beanClassList = resolveClassWithAnnotation(annotation);
        for (Class beanClass : beanClassList) {
            getBean(beanClass);
        }
    }

    private Object getBean(Class beanClass) throws Exception {
        Object beanObj = beansMap.get(beanClass.getName());
        if (Objects.nonNull(beanObj)) {
            return beanObj;
        }
        Object beanInstance = beanClass.newInstance();
        //设置属性值
        autowireToBean(beanInstance);

        beanClassAndOriginObjectCache.put(beanClass, beanInstance);
        Transactional declaredAnnotation = (Transactional) beanClass.getAnnotation(Transactional.class);
        if (Objects.nonNull(declaredAnnotation)) {
            beanInstance = createBeanProxy(beanInstance);
        }
        Class[] interfaces = beanClass.getInterfaces();
        for (Class anInterface : interfaces) {
            beansMap.put(anInterface.getName(), beanInstance);
        }
        beansMap.put(beanClass.getName(), beanInstance);
        return beanInstance;
    }

    private Object createBeanProxy(Object beanObj) throws Exception {
        ProxyFactory proxyFactory = (ProxyFactory) getBean(ProxyFactory.class);
        Class[] interfaces = beanObj.getClass().getInterfaces();
        if (interfaces.length > 0) {
            //创建jdk代理
            return proxyFactory.getJdkProxy(beanObj);
        } else {
            //创建cglib代理
            return proxyFactory.getCglibProxy(beanObj);
        }
    }


    public void autowireToBean(Object beanInstance) throws Exception {
        Field[] declaredFields = beanInstance.getClass().getDeclaredFields();
        List<Field> fieldsNeedAutowire = Arrays.stream(declaredFields)
                .filter(declaredField -> Objects.nonNull(declaredField.getAnnotation(Autowire.class)))
                .collect(Collectors.toList());
        setFieldToBean(fieldsNeedAutowire, beanInstance);

    }

    private void setFieldToBean(List<Field> fieldsNeedAutowire, Object bean) throws Exception {
        for (Field field : fieldsNeedAutowire) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            Class<?> fieldDeclaringClass = field.getType();
            Object fieldBean = getBean(fieldDeclaringClass);
            field.set(bean, fieldBean);
        }
    }

    public Map<String, Object> getBeansMap() {
        return beansMap;
    }
}
