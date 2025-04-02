package com.github.dawndev.orion.core.lang.method;

import com.github.dawndev.orion.core.exception.MethodInvokeException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class MethodInvocationWrap implements MethodInvocation {
    private Object context;
    private String name;
    private Method checkMethod;
    private Method taskMethod;
    protected Class<?>[] paramTypes;
    protected Class<?>[] compTypes;

    public MethodInvocationWrap(Object context, String name, Method checkMethod, Method taskMethod) {
        this.context = context;
        this.name = name;
        this.checkMethod = checkMethod;
        this.taskMethod = taskMethod;
        this.paramTypes = taskMethod.getParameterTypes();
        int parmsLength = getParamsLength();
        this.compTypes = new Class<?>[parmsLength];
        for (int i = 0; i < parmsLength; ++i) {
            compTypes[i] = paramTypes[i].getComponentType();
        }
    }

    public String getName() {
        return name;
    }

    public Method getTaskMethod() {
        return taskMethod;
    }

    public int getParamsLength() {
        return paramTypes.length;
    }

    public Object invoke(Object context, Object[] params) {
        try {
            if (params != null) {
                Class<?> paramType;
                Class<?> compType;
                Object param;
                Object[] newParams = new Object[getParamsLength()];
                for (int i = 0; i < newParams.length; ++i) {
                    paramType = paramTypes[i];
                    compType = compTypes[i];
                    param = i < params.length ? params[i] : null;
                    if (param != null && paramType.isArray() && compType != null) {
                        //newParams[i] = ReflectionUtils.convertArray(param, compType);
                    } else if (paramType.isPrimitive() || Number.class.isAssignableFrom(paramType)) {
                        //newParams[i] = ReflectionUtils.convertPrimitive(param, paramType);
                    } else {
                        newParams[i] = param;
                    }
                }
                return taskMethod.invoke(context, newParams);
            }
            return taskMethod.invoke(context);
        } catch (InvocationTargetException e) {
            throw new MethodInvokeException("invoke method exception for name: " + name, e.getTargetException());
        } catch (Exception e) {
            throw new MethodInvokeException("invoke method exception for name: " + name, e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof MethodInvocationWrap)) {
            return false;
        }
        MethodInvocationWrap tObj = (MethodInvocationWrap) obj;
        if ((getName() == null && tObj.getName() != null) || (getName() != null && !getName().equals(tObj.getName()))) {
            return false;
        }
        if ((getTaskMethod() == null && tObj.getTaskMethod() != null)
                || (getTaskMethod() != null && !getTaskMethod().equals(tObj.getTaskMethod()))) {
            return false;
        }
        return true;
    }

}