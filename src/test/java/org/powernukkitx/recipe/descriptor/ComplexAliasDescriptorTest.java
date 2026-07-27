package org.powernukkitx.recipe.descriptor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ComplexAliasDescriptorTest {

    @Test
    void getterReturnsConstructorArg() {
        ComplexAliasDescriptor descriptor = new ComplexAliasDescriptor("alias_name");
        Assertions.assertEquals("alias_name", descriptor.getName());
    }

    @Test
    void typeIsComplexAlias() {
        Assertions.assertEquals(ItemDescriptorType.COMPLEX_ALIAS, new ComplexAliasDescriptor("x").getType());
    }

    @Test
    void countIsZero() {
        Assertions.assertEquals(0, new ComplexAliasDescriptor("x").getCount());
    }

    @Test
    void equalsAndHashCodeAreConsistent() {
        ComplexAliasDescriptor a = new ComplexAliasDescriptor("a");
        ComplexAliasDescriptor b = new ComplexAliasDescriptor("a");
        ComplexAliasDescriptor c = new ComplexAliasDescriptor("b");

        Assertions.assertEquals(a, b);
        Assertions.assertEquals(a.hashCode(), b.hashCode());
        Assertions.assertNotEquals(a, c);
    }

    @Test
    void toItemThrows() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> new ComplexAliasDescriptor("x").toItem());
    }
}
