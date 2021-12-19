package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.potion.Potion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author joserobjr
 * @since 2021-12-18
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
class ItemArrowTest {
    ItemArrow arrow;

    @BeforeEach
    void setUp() {
        arrow = new ItemArrow();
    }

    @Test
    void getTippedArrowEffect() {
        assertNull(arrow.getTippedArrowPotion());
        for (int damage = 1; damage <= 43; damage++) {
            arrow.setDamage(damage);
            Potion potion = Potion.getPotion(damage - 1);
            assertEquals(potion, arrow.getTippedArrowPotion());
        }
        arrow.setDamage(100);
        assertNull(arrow.getTippedArrowPotion());
    }
}
