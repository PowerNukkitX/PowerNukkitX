package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.api.MockPlayer;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;

/**
 * @author joserobjr
 * @since 2021-12-19
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
class ItemArmorTest {
    @MockPlayer
    Player player;

    @Test
    void onClickAir() {
        ItemArmor armor = (ItemArmor) Item.get(ItemID.IRON_CHESTPLATE);
        player.getInventory().setItemInHand(armor);
        armor.onClickAir(player, new Vector3());
        verify(player.getLevel()).addSound(same(player), eq(Sound.ARMOR_EQUIP_IRON));
    }
}
