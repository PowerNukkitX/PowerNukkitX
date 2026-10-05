package org.powernukkitx.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.entity.effect.PotionType;
import org.powernukkitx.registry.Registries;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PotionItemFactoryTest {

    @BeforeAll
    static void initPotionRegistry() {
        Registries.POTION.init();
    }

    @Test
    void createsSplashPotionFromPotionType() {
        ItemSplashPotion potion = ItemSplashPotion.fromPotion(PotionType.HEALING_STRONG);

        assertEquals(PotionType.HEALING_STRONG.id(), potion.getDamage());
        assertEquals(1, potion.getCount());
    }

    @Test
    void createsLingeringPotionFromPotionType() {
        ItemLingeringPotion potion = ItemLingeringPotion.fromPotion(PotionType.POISON_LONG);

        assertEquals(PotionType.POISON_LONG.id(), potion.getDamage());
        assertEquals(1, potion.getCount());
    }
}
