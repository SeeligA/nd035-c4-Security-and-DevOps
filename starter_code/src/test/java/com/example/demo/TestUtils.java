package com.example.demo;

import junit.framework.Test;

import java.lang.reflect.Field;

public class TestUtils {
    public static void injectObjects(Object target, String fieldName, Object toInject){

        boolean wasPrivate = false;
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            wasPrivate = !f.isAccessible();
            f.trySetAccessible();
            f.set(target, toInject);
            f.setAccessible(wasPrivate);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
