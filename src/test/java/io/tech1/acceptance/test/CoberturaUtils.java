package io.tech1.acceptance.test;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CoberturaUtils {

    public static void classCoverageHook(Class<?> clazz) {
        try {
            defaultConstructorHook(clazz);
        } catch (ReflectiveOperationException e1) {
            log.error("Cannot initialize clazz by default constructor: " + clazz.getCanonicalName(), e1);
            try {
                privateConstructorHook(clazz);
            } catch (ReflectiveOperationException e2) {
                log.error("Cannot initialize clazz by private constructor: " + clazz.getCanonicalName(), e2);
            }
        }
    }

    private static void defaultConstructorHook(Class<?> clazz) throws ReflectiveOperationException {
        clazz.newInstance();
    }

    private static void privateConstructorHook(Class<?> clazz) throws ReflectiveOperationException {
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
