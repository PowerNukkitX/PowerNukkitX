package org.powernukkitx.registry;

import org.powernukkitx.item.Item;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * A registry without recipes of some type - recipes disabled in the gameplay settings, or a type no
 * data provides - must answer lookups with an empty result rather than failing.
 */
class RecipeRegistryEmptyTest {

    @Test
    void lookupsOnEmptyRegistryReturnNothing() throws IllegalAccessException {
        RecipeRegistry registry = new RecipeRegistry();
        int checked = 0;
        for (Method method : RecipeRegistry.class.getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            boolean getter = method.getName().startsWith("get") && method.getName().endsWith("RecipeMap")
                    && method.getParameterCount() == 0;
            boolean finder = method.getName().startsWith("find") && method.getParameterCount() == 1
                    && method.getParameterTypes()[0] == Item[].class;
            if (!getter && !finder) {
                continue;
            }
            Object result;
            try {
                result = getter ? method.invoke(registry) : method.invoke(registry, (Object) new Item[0]);
            } catch (InvocationTargetException e) {
                fail(method.getName() + " failed on an empty registry", e.getCause());
                return;
            }
            if (result instanceof Map<?, ?> map) {
                assertTrue(map.isEmpty(), method.getName());
            } else if (getter) {
                assertTrue(((Collection<?>) result).isEmpty(), method.getName());
            } else {
                assertNull(result, method.getName());
            }
            checked++;
        }
        assertTrue(checked > 20, "expected to exercise every recipe getter and finder, checked " + checked);
    }
}
