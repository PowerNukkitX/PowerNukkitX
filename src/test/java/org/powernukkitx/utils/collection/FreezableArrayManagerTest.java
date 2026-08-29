package org.powernukkitx.utils.collection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

class FreezableArrayManagerTest {

    private FreezableArrayManager disabledManager() {
        // enable=false so byte arrays are plain PureByteArray - no Server dependency
        return new FreezableArrayManager(false, 32, 32, 0, -256, 1024, 16, 1, 32);
    }

    @Test
    void gettersReflectConstructorArgs() {
        var m = disabledManager();
        assertFalse(m.enable);
        assertEquals(32, m.cycleTick);
        assertEquals(32, m.getDefaultTemperature());
        assertEquals(0, m.getFreezingPoint());
        assertEquals(-256, m.getAbsoluteZero());
        assertEquals(1024, m.getBoilingPoint());
        assertEquals(16, m.getMeltingHeat());
        assertEquals(1, m.getSingleOperationHeat());
        assertEquals(32, m.getBatchOperationHeat());
    }

    @Test
    void setMaxCompressionTimeIsChainableAndStored() {
        var m = disabledManager();
        var returned = m.setMaxCompressionTime(123);
        assertSame(m, returned);
        assertEquals(123, m.getMaxCompressionTime());
    }

    @Test
    void createByteArrayAllocatesRequestedLength() {
        var arr = disabledManager().createByteArray(4);
        assertEquals(4, arr.getRawBytes().length);
        arr.setByte(0, (byte) 7);
        assertEquals((byte) 7, arr.getByte(0));
    }

    @Test
    void wrapByteArrayKeepsSameBackingArray() {
        byte[] src = {1, 2, 3};
        var arr = disabledManager().wrapByteArray(src);
        assertSame(src, arr.getRawBytes());
    }

    @Test
    void cloneByteArrayCopiesContent() {
        byte[] src = {1, 2, 3};
        var arr = disabledManager().cloneByteArray(src);
        assertNotSame(src, arr.getRawBytes());
        assertArrayEquals(src, arr.getRawBytes());
    }

    @Test
    void setRawBytesReplacesBacking() {
        var arr = disabledManager().createByteArray(2);
        byte[] replacement = {9, 9, 9};
        arr.setRawBytes(replacement);
        assertSame(replacement, arr.getRawBytes());
    }
}
