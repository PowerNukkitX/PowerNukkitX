package cn.nukkit.math;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author joserobjr
 * @since 2021-12-19
 */
@PowerNukkitOnly
@Since("FUTURE")
class BlockVector3Test {
    BlockVector3 vector3;
    @Test
    void setGet() {
        vector3 = new BlockVector3();
        vector3.setX(1);
        vector3.setY(2);
        vector3.setZ(3);
        assertEquals(1, vector3.getX());
        assertEquals(2, vector3.getY());
        assertEquals(3, vector3.getZ());
        assertNotSame(vector3, vector3.clone());
        assertEquals(vector3, vector3.clone());
    }
}
