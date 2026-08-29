package org.powernukkitx.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BinaryTest {
    @Test
    void bytesToHexStringBasic() {
        Assertions.assertEquals("00", Binary.bytesToHexString(new byte[]{0}));
        Assertions.assertEquals("FF", Binary.bytesToHexString(new byte[]{(byte) 0xFF}));
        Assertions.assertEquals("0A1B", Binary.bytesToHexString(new byte[]{0x0A, 0x1B}));
    }

    @Test
    void bytesToHexStringUppercase() {
        Assertions.assertEquals("DEADBEEF", Binary.bytesToHexString(new byte[]{(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF}));
    }

    @Test
    void bytesToHexStringBlankSeparator() {
        Assertions.assertEquals("0A 1B FF", Binary.bytesToHexString(new byte[]{0x0A, 0x1B, (byte) 0xFF}, true));
    }

    @Test
    void bytesToHexStringNullOrEmpty() {
        Assertions.assertNull(Binary.bytesToHexString(null));
        Assertions.assertNull(Binary.bytesToHexString(new byte[0]));
    }
}
