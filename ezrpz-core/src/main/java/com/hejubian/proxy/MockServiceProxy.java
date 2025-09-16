package com.hejubian.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public class MockServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> returnType = method.getReturnType();
        log.info("mock invoke {}",  method.getName());
        return getDefaultObject(returnType);
    }

    /**
     *   获取默认值
     * @param clazz
     * @return
     */
    private Object getDefaultObject(Class<?> clazz){
        if (clazz == boolean.class){
            return false;
        } else  if (clazz == short.class){
            return  (short) 0;
        } else  if (clazz == int.class){
            return 0;
        }  else  if (clazz == long.class){
            return 0L;
        }
        return  null;
    }
}
