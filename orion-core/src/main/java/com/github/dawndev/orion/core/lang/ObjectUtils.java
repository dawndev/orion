package com.github.dawndev.orion.core.lang;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ObjectUtils {
    /**
     * 获取方法列表
     * @param targetClass 模板class
     * @return 方法名和方法
     */
    public static Map<String, Method> getDeclaredMethodMap(Class<?> targetClass) {
        Map<String, Method> declaredMethodMap = new HashMap<>();
        for (Class<?> clazz = targetClass; clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (Method method : clazz.getDeclaredMethods()) {
                declaredMethodMap.put(method.getName(), method);
            }
        }
        return declaredMethodMap;
    }
}
