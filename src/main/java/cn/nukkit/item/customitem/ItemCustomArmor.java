package cn.nukkit.item.customitem;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemArmor;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.StringItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author lt_name
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class ItemCustomArmor extends ItemArmor implements CustomItem {
    private final String id;
    private final String textureName;

    public ItemCustomArmor(@NotNull String id, @Nullable String name) {
        super(ItemID.STRING_IDENTIFIED_ITEM, 0, 1, StringItem.notEmpty(name));
        this.id = id;
        this.textureName = name;
    }

    public ItemCustomArmor(@NotNull String id, @Nullable String name, @NotNull String textureName) {
        super(ItemID.STRING_IDENTIFIED_ITEM, 0, 1, StringItem.notEmpty(name));
        this.id = id;
        this.textureName = textureName;
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
}
