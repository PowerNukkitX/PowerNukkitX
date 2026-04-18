package cn.nukkit.item.customitem;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.registry.Registries;
import org.cloudburstmc.nbt.NbtType;
import org.jetbrains.annotations.NotNull;

/**
 * @deprecated Extend Item directly and implement CustomItem; custom-item hooks are now in Item and <p>
 * definitions use CustomItemDefinition.SimpleBuilder, so this wrapper is no longer needed. <p>
 * Prefer to extend Item class {@link Item} while implementing {@link CustomItem} for customized items.
 * @author lt_name
 */
@Deprecated
public abstract class ItemCustomTool extends ItemTool implements CustomItem {
    public ItemCustomTool(@NotNull String id) {
        super(id);
    }

    @Override
    public int getMaxDurability() {
        return DURABILITY_WOODEN;
    }

    /**
     * @deprecated prefer to use {@link Item#getDiggerSpeed} instead.
     */
    @Deprecated
    public final Integer getSpeed() {
        var nbt = Registries.ITEM.getCustomItemDefinition().get(this.getId()).nbt();
        if (nbt == null || !nbt.getCompound("components").containsKey("minecraft:digger")) return null;
        return nbt.getCompound("components")
                .getCompound("minecraft:digger")
                .getList("destroy_speeds", NbtType.COMPOUND).getFirst().getInt("speed");
    }
}
