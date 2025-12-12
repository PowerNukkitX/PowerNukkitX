package cn.nukkit.item.enchantment.loot;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;

import java.util.Set;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentLootWeapon extends EnchantmentLoot {

    private final Set<String> IGNORED = Set.of(
            Entity.BAT,
            Entity.BEE,
            Entity.ENDERMITE,
            Entity.WITHER,
            Entity.IRON_GOLEM,
            Entity.CAT,
            Entity.SNOW_GOLEM,
            Entity.PUFFERFISH,
            Entity.SALMON,
            Entity.COD,
            Entity.TROPICALFISH,
            Entity.PANDA,
            Entity.SHEEP,
            Entity.ELDER_GUARDIAN,
            Entity.EVOCATION_ILLAGER,
            Entity.WARDEN,
            Entity.WITHER_SKELETON
    );

    public EnchantmentLootWeapon() {
        super(Enchantment.ID_LOOTING, "lootBonus", Rarity.RARE, EnchantmentType.SWORD);
    }

    public boolean isIgnored(Entity entity) {
        return IGNORED.contains(entity.getIdentifier());
    }
}
