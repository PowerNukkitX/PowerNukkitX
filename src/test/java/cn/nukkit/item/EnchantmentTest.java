package cn.nukkit.item;

import cn.nukkit.GameMockExtension;
import cn.nukkit.item.enchantment.EnchantmentHelper;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.PlayerEnchantOptionsPacket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@ExtendWith(GameMockExtension.class)
public class EnchantmentTest {
    @Test
    void testEnchantmentHelper(Level level) {
        List<PlayerEnchantOptionsPacket.EnchantOptionData> enchantOptions1 = EnchantmentHelper.getEnchantOptions(new Position(0, 0, 0, level), Item.get(ItemID.DIAMOND_SWORD), 114514);
        List<PlayerEnchantOptionsPacket.EnchantOptionData> enchantOptions2 = EnchantmentHelper.getEnchantOptions(new Position(0, 0, 0, level), Item.get(ItemID.DIAMOND_SWORD), 114514);
        Assertions.assertEquals(enchantOptions1, enchantOptions2);
    }
}
