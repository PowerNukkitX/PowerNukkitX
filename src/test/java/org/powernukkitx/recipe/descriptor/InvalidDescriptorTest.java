package org.powernukkitx.recipe.descriptor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InvalidDescriptorTest {

    @Test
    void instanceIsSingletonAndNonNull() {
        Assertions.assertNotNull(InvalidDescriptor.INSTANCE);
        Assertions.assertSame(InvalidDescriptor.INSTANCE, InvalidDescriptor.INSTANCE);
    }

    @Test
    void typeIsInvalid() {
        Assertions.assertEquals(ItemDescriptorType.INVALID, InvalidDescriptor.INSTANCE.getType());
    }

    @Test
    void countIsZero() {
        Assertions.assertEquals(0, InvalidDescriptor.INSTANCE.getCount());
    }

    @Test
    void toStringContainsCount() {
        Assertions.assertTrue(InvalidDescriptor.INSTANCE.toString().contains("0"));
    }

    @Test
    void toItemThrows() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> InvalidDescriptor.INSTANCE.toItem());
    }
}
