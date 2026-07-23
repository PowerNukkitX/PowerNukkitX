package org.powernukkitx.recipe.descriptor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ItemDescriptorTypeTest {

    @Test
    void containsExpectedConstants() {
        Assertions.assertEquals(6, ItemDescriptorType.values().length);
        Assertions.assertNotNull(ItemDescriptorType.valueOf("INVALID"));
        Assertions.assertNotNull(ItemDescriptorType.valueOf("DEFAULT"));
        Assertions.assertNotNull(ItemDescriptorType.valueOf("MOLANG"));
        Assertions.assertNotNull(ItemDescriptorType.valueOf("ITEM_TAG"));
        Assertions.assertNotNull(ItemDescriptorType.valueOf("DEFERRED"));
        Assertions.assertNotNull(ItemDescriptorType.valueOf("COMPLEX_ALIAS"));
    }

    @Test
    void valueOf_roundTripsForEveryConstant() {
        for (ItemDescriptorType type : ItemDescriptorType.values()) {
            Assertions.assertSame(type, ItemDescriptorType.valueOf(type.name()));
        }
    }
}
