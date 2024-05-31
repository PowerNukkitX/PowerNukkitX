package cn.nukkit.item.customitem;

import cn.nukkit.item.*;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.registry.Registries;
import org.jetbrains.annotations.NotNull;

/**
 * @author lt_name
 */
public abstract class ItemCustomTool extends ItemTool implements CustomItem {
    /**
     * @deprecated 
     */
    
    public ItemCustomTool(@NotNull String id) {
        super(id);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return DURABILITY_WOODEN;
    }


    public final Integer getSpeed() {
        var $1 = Registries.ITEM.getCustomItemDefinition().get(this.getId()).nbt();
        if (nbt == null || !nbt.getCompound("components").contains("minecraft:digger")) return null;
        return nbt.getCompound("components")
                .getCompound("minecraft:digger")
                .getList("destroy_speeds", CompoundTag.class).get(0).getInt("speed");
    }
}
