package cn.nukkit.item.customitem;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.*;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author lt_name
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class ItemCustomTool extends ItemTool implements ItemDurable, CustomItem {
    private final String id;
    private final String textureName;

    public ItemCustomTool(@NotNull String id, @Nullable String name) {
        super(ItemID.STRING_IDENTIFIED_ITEM, 0, 1, StringItem.notEmpty(name));
        this.id = id;
        this.textureName = name;
    }

    public ItemCustomTool(@NotNull String id, @Nullable String name, @NotNull String textureName) {
        super(ItemID.STRING_IDENTIFIED_ITEM, 0, 1, StringItem.notEmpty(name));
        this.id = id;
        this.textureName = textureName;
    }

    @Override
    public int getMaxDurability() {
        return DURABILITY_WOODEN;
    }

    public String getTextureName() {
        return textureName;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public String getNamespaceId() {
        return id;
    }

    @Override
    public final int getId() {
        return CustomItem.super.getId();
    }

    public final Integer getSpeed() {
        var nbt = Item.getCustomItemDefinition().get(this.getNamespaceId()).nbt();
        if (nbt == null || !nbt.getCompound("components").contains("minecraft:digger")) return null;
        return nbt.getCompound("components")
                .getCompound("minecraft:digger")
                .getList("destroy_speeds", CompoundTag.class).get(0).getInt("speed");
    }
}
