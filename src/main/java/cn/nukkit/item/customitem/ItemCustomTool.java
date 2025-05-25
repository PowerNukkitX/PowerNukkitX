package cn.nukkit.item.customitem;

import cn.nukkit.item.ItemTool;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.registry.Registries;
import org.jetbrains.annotations.NotNull;

/**
 * @author lt_name
 */
public abstract class ItemCustomTool extends ItemTool implements CustomItem {
    public ItemCustomTool(@NotNull String id) {
        super(id);
    }

    @Override
    public int getMaxDurability() {
        return DURABILITY_WOODEN;
    }


    public final Integer getSpeed() {
        var nbt = Registries.ITEM.getCustomItemDefinition().get(this.getId()).nbt();
        if (nbt == null || !nbt.getCompound("components").contains("minecraft:digger")) return null;
        return nbt.getCompound("components")
                .getCompound("minecraft:digger")
                .getList("destroy_speeds", CompoundTag.class).get(0).getInt("speed");
    }
}
