package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.item.food.Food;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@PowerNukkitXOnly
@ExtendWith(PowerNukkitExtension.class)
class FoodTest {
    @Test
    void foodToItem() {
        assertEquals(Food.apple.getItem(), Item.get(ItemID.APPLE));
    }
}
