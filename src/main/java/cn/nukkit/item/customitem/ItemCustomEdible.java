package cn.nukkit.item.customitem;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemEdible;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.StringItem;
import cn.nukkit.item.food.Food;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.Identifier;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author lt_name
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class ItemCustomEdible extends ItemEdible implements CustomItem {
    private final String id;
    private final String textureName;

    public ItemCustomEdible(@NotNull String id, @Nullable String name) {
        super(ItemID.STRING_IDENTIFIED_ITEM, 0, 1, StringItem.notEmpty(name));
        Identifier.assertValid(id);
        this.id = id;
        this.textureName = name;
    }

    public ItemCustomEdible(@NotNull String id, @Nullable String name, @NotNull String textureName) {
        super(ItemID.STRING_IDENTIFIED_ITEM, 0, 1, StringItem.notEmpty(name));
        Identifier.assertValid(id);
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

    @Since("1.19.60-r1")
    public abstract Map.Entry<Plugin, Food> getFood();

    public boolean isDrink() {
        return false;
    }

    public boolean canAlwaysEat() {
        return this.isDrink();
    }
}
