package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
