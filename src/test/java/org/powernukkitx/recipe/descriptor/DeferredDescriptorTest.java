package org.powernukkitx.recipe.descriptor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DeferredDescriptorTest {

    @Test
    void gettersReturnConstructorArgs() {
        DeferredDescriptor descriptor = new DeferredDescriptor("minecraft:stone", 2, 4);
        Assertions.assertEquals("minecraft:stone", descriptor.getFullName());
        Assertions.assertEquals(2, descriptor.getAuxValue());
        Assertions.assertEquals(4, descriptor.getCount());
    }

    @Test
    void typeIsDeferred() {
        Assertions.assertEquals(ItemDescriptorType.DEFERRED, new DeferredDescriptor("x", 0, 1).getType());
    }

    @Test
    void equalsAndHashCodeAreConsistent() {
        DeferredDescriptor a = new DeferredDescriptor("minecraft:stone", 1, 2);
        DeferredDescriptor b = new DeferredDescriptor("minecraft:stone", 1, 2);
        DeferredDescriptor differByName = new DeferredDescriptor("minecraft:dirt", 1, 2);
        DeferredDescriptor differByAux = new DeferredDescriptor("minecraft:stone", 9, 2);
        DeferredDescriptor differByCount = new DeferredDescriptor("minecraft:stone", 1, 9);

        Assertions.assertEquals(a, b);
        Assertions.assertEquals(a.hashCode(), b.hashCode());
        Assertions.assertNotEquals(a, differByName);
        Assertions.assertNotEquals(a, differByAux);
        Assertions.assertNotEquals(a, differByCount);
    }

    @Test
    void toStringContainsFields() {
        String s = new DeferredDescriptor("minecraft:stone", 3, 5).toString();
        Assertions.assertTrue(s.contains("minecraft:stone"));
        Assertions.assertTrue(s.contains("3"));
        Assertions.assertTrue(s.contains("5"));
    }

    @Test
    void toItemThrows() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> new DeferredDescriptor("x", 0, 1).toItem());
    }
}
