package cn.nukkit.item.enchantment.damage;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author joserobjr
 * @since 2021-12-20
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
class EnchantmentDamageAllTest {
    @Mock
    Entity entity;
    EnchantmentDamageAll enchant;

    @BeforeEach
    void setUp() {
        enchant = new EnchantmentDamageAll();
    }

    @Test
    void getDamageBonus() {
        enchant.setLevel(-1, false);
        assertEquals(0, enchant.getDamageBonus(entity));

        enchant.setLevel(1, false);
        assertEquals(1.25, enchant.getDamageBonus(entity));

        enchant.setLevel(2, false);
        assertEquals(2.5, enchant.getDamageBonus(entity));

        enchant.setLevel(3, false);
        assertEquals(3.75, enchant.getDamageBonus(entity));

        enchant.setLevel(4, false);
        assertEquals(5.00, enchant.getDamageBonus(entity));

        assertEquals(4, enchant.getMaxEnchantableLevel());
    }
}
