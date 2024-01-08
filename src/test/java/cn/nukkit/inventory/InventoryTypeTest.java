package cn.nukkit.inventory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InventoryTypeTest {

    @Test
    void testCursor() {
        Assertions.assertNotNull(InventoryType.CURSOR);
    }
}
