package cn.nukkit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(PowerNukkitExtension.class)
class ItemTest {
    static Method loadCreativeItemEntry;
    @BeforeAll
    static void beforeAll() throws NoSuchMethodException {
        loadCreativeItemEntry = Item.class.getDeclaredMethod("loadCreativeItemEntry", Map.class);
        loadCreativeItemEntry.setAccessible(true);
    }

    @Test
    void loadCreativeItemEntryBlockState() throws ReflectiveOperationException {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("blockState", "xxxxxx");
        assertNull(loadCreativeItemEntry(data));
        data.put("blockState", "minecraft:air;invalid=1");
        assertNull(loadCreativeItemEntry(data));
    }

    @SneakyThrows
    private Item loadCreativeItemEntry(Map<String, Object> data) {
        return (Item) loadCreativeItemEntry.invoke(null, data);
    }
}
