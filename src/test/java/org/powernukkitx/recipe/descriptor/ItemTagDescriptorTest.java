package org.powernukkitx.recipe.descriptor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ItemTagDescriptorTest {

    @Test
    void gettersReturnConstructorArgs() {
        ItemTagDescriptor descriptor = new ItemTagDescriptor("minecraft:planks", 3);
        Assertions.assertEquals("minecraft:planks", descriptor.getItemTag());
        Assertions.assertEquals(3, descriptor.getCount());
    }

    @Test
    void typeIsItemTag() {
        Assertions.assertEquals(ItemDescriptorType.ITEM_TAG, new ItemTagDescriptor("x", 1).getType());
    }

    @Test
    void equalsAndHashCodeAreConsistent() {
        ItemTagDescriptor a = new ItemTagDescriptor("minecraft:logs", 2);
        ItemTagDescriptor b = new ItemTagDescriptor("minecraft:logs", 2);
        ItemTagDescriptor c = new ItemTagDescriptor("minecraft:logs", 5);
        ItemTagDescriptor d = new ItemTagDescriptor("minecraft:wool", 2);

        Assertions.assertEquals(a, b);
        Assertions.assertEquals(a.hashCode(), b.hashCode());
        Assertions.assertNotEquals(a, c);
        Assertions.assertNotEquals(a, d);
        Assertions.assertNotEquals(a, null);
    }

    @Test
    void toStringContainsFields() {
        String s = new ItemTagDescriptor("minecraft:planks", 7).toString();
        Assertions.assertTrue(s.contains("minecraft:planks"));
        Assertions.assertTrue(s.contains("7"));
    }

    @Test
    void toItemThrows() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> new ItemTagDescriptor("x", 1).toItem());
    }
}
