package cn.nukkit.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import org.junit.jupiter.api.Test;

/**
 * @author joserobjr
 * @since 2021-12-13
 */
@PowerNukkitOnly
@Since("FUTURE")
class BinaryTest {
    @Test
    void writeReadLInt() {
        final byte[] bytes = Binary.writeLInt(1065353216);
        assertEquals(1065353216, Binary.readLInt(bytes));
    }
}
