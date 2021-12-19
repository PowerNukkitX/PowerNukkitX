package cn.nukkit.potion;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author joserobjr
 * @since 2021-12-19
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
class PotionTest {
    @Test
    void names() {
        assertEquals("Water Potion", Potion.getPotion(Potion.WATER).getName());
        assertEquals("Mundane Potion", Potion.getPotion(Potion.MUNDANE).getName());
        assertEquals("Mundane Potion II", Potion.getPotion(Potion.MUNDANE_II).getName());
        assertEquals("Thick Potion", Potion.getPotion(Potion.THICK).getName());
        assertEquals("Awkward Potion", Potion.getPotion(Potion.AWKWARD).getName());
        assertEquals("Potion", new Potion(100000).getName());
        assertEquals("Potion", new Potion(100000).getName());
        assertEquals("Potion of the Turtle Master II", Potion.getPotion(Potion.TURTLE_MASTER_II).getName());
        assertEquals("Potion of Slowness IV", Potion.getPotion(Potion.SLOWNESS_IV).getName());
    }
}
