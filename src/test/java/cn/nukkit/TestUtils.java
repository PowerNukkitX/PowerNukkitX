package cn.nukkit;

import java.lang.reflect.Field;

public class TestUtils {
    public static void setField(Class<?> clazz, Object target, String fieldName, Object value) {
        try {
            Field infoF = clazz.getDeclaredField(fieldName);
            infoF.setAccessible(true);
            infoF.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
