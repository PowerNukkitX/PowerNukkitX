package cn.nukkit.level.terra.handles;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.terra.PNXAdapter;
import cn.nukkit.level.terra.delegate.PNXEnchantmentDelegate;
import com.dfsek.terra.api.handle.ItemHandle;
import com.dfsek.terra.api.inventory.Item;
import com.dfsek.terra.api.inventory.item.Enchantment;

import java.util.Set;
import java.util.stream.Collectors;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public class PNXItemHandle implements ItemHandle {
    @Override
    public Item createItem(String s) {
        return PNXAdapter.adapt(cn.nukkit.item.Item.fromString(s));
    }

    @Override
    public Enchantment getEnchantment(String s) {
        if (s.startsWith("minecraft:")) s = s.substring(10);
        return new PNXEnchantmentDelegate(cn.nukkit.item.enchantment.Enchantment.getEnchantment(switch (s) {
            case "protection" -> 0;
            case "fire_protection" -> 1;
            case "feather_falling" -> 2;
            case "blast_protection" -> 3;
            case "projectile_protection" -> 4;
            case "thorns" -> 5;
            case "respiration" -> 6;
            case "depth_strider" -> 7;
            case "aqua_affinity" -> 8;
            case "sharpness" -> 9;
            case "smite" -> 10;
            case "bane_of_arthropods" -> 11;
            case "knockback" -> 12;
            case "fire_aspect" -> 13;
            case "looting" -> 14;
            case "efficiency" -> 15;
            case "silk_touch" -> 16;
            case "unbreaking" -> 17;
            case "fortune" -> 18;
            case "power" -> 19;
            case "punch" -> 20;
            case "flame" -> 21;
            case "infinity" -> 22;
            case "luck_of_the_sea" -> 23;
            case "lure" -> 24;
            case "frost_walker" -> 25;
            case "mending" -> 26;
            case "binding_curse" -> 27;
            case "vanishing_curse" -> 28;
            case "impaling" -> 29;
            case "riptide" -> 30;
            case "loyalty" -> 31;
            case "channeling" -> 32;
            case "multishot" -> 33;
            case "piercing" -> 34;
            case "quick_charge" -> 35;
            case "soul_speed" -> 36;
            default -> throw new IllegalStateException("Unexpected enchantment id: " + s);
        }));
    }

    @Override
    public Set<Enchantment> getEnchantments() {
        return cn.nukkit.item.enchantment.Enchantment.getRegisteredEnchantments().stream()
                .map(PNXEnchantmentDelegate::new)
                .collect(Collectors.toSet());
    }
}
