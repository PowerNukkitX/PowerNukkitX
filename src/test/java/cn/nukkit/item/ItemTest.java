package cn.nukkit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@ExtendWith(PowerNukkitExtension.class)
class ItemTest {
    static Method loadCreativeItemEntry;
    static Method initCreativeItems;

    @BeforeAll
    static void beforeAll() throws NoSuchMethodException {
        loadCreativeItemEntry = Item.class.getDeclaredMethod("loadCreativeItemEntry", Map.class);
        initCreativeItems = Item.class.getDeclaredMethod("initCreativeItems");
        loadCreativeItemEntry.setAccessible(true);
        initCreativeItems.setAccessible(true);
    }

    @Test
    void initCreativeItemsTest() throws InvocationTargetException, IllegalAccessException {
        initCreativeItems.invoke(null);
    }

    @SneakyThrows
    private Item loadCreativeItemEntry(Map<String, Object> data) {
        return (Item) loadCreativeItemEntry.invoke(null, data);
    }
}
