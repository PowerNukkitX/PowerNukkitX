package org.powernukkitx.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.entity.effect.PotionApplicationMode;
import org.powernukkitx.entity.effect.PotionType;
import org.powernukkitx.registry.Registries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class PotionItemFactoryTest {

    @BeforeAll
    static void initPotionRegistry() {
        Registries.POTION.init();
    }

    @Test
    void createsDrinkablePotionFromPotionType() {
        Item potion = PotionType.REGENERATION.getItem(PotionApplicationMode.DRINK);

        assertInstanceOf(ItemPotion.class, potion);
        assertEquals(PotionType.REGENERATION.id(), potion.getDamage());
        assertEquals(1, potion.getCount());
    }

    @Test
    void createsSplashPotionFromPotionType() {
        Item potion = PotionType.HEALING_STRONG.getItem(PotionApplicationMode.SPLASH);

        assertInstanceOf(ItemSplashPotion.class, potion);
        assertEquals(PotionType.HEALING_STRONG.id(), potion.getDamage());
        assertEquals(1, potion.getCount());
    }

    @Test
    void createsLingeringPotionFromPotionType() {
        Item potion = PotionType.POISON_LONG.getItem(PotionApplicationMode.LINGERING);

        assertInstanceOf(ItemLingeringPotion.class, potion);
        assertEquals(PotionType.POISON_LONG.id(), potion.getDamage());
        assertEquals(1, potion.getCount());
    }

    @Test
    void createsTippedArrowFromPotionType() {
        Item arrow = PotionType.SLOWNESS_STRONG.getItem(PotionApplicationMode.ARROW);

        assertInstanceOf(ItemArrow.class, arrow);
        assertEquals(PotionType.SLOWNESS_STRONG.id() + 1, arrow.getDamage());
        assertEquals(1, arrow.getCount());
    }
}
