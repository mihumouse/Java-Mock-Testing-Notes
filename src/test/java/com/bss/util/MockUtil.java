package com.bss.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.powermock.api.mockito.PowerMockito;

public class MockUtil {

    /**
     * common method of calling private method
     * @param <T>         target test class
     * @param instance    target test instance of class
     * @param methodName  tartet private method
     * @param paras       parameters of the private method
     * @return private method's return
     */
    public static <T extends Object> Object runPrivateMethod(T instance, String methodName, Object... paras) {
        Class[] paraTypes = new Class[paras.length];
        for (int i = 0; i < paras.length; i++) {
            paraTypes[i] = paras[i].getClass();
        }

        Class clazz = instance.getClass();
        Method method = PowerMockito.method(clazz, methodName, paraTypes);
        
        Object returnObj = null;
        try {
            returnObj = method.invoke(instance, paras);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // 反射后抛出异常，mock机制无法判断，处理再抛出
            try {
                throw e.getCause();
            } catch (Throwable e1) {
                e1.printStackTrace();
            }
        }
        return returnObj;
    }
}