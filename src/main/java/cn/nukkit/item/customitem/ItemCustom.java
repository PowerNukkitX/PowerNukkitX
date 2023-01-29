package cn.nukkit.item.customitem;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.StringItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 继承这个类实现自定义物品,重写{@link Item}中的方法控制方块属性
 * <p>
 * Inherit this class to implement a custom item, override the methods in the {@link Item} to control the feature of the item.
 *
 * @author lt_name
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class ItemCustom extends Item implements CustomItem {
    private final String id;
    private final String textureName;

    public ItemCustom(@Nonnull String id, @Nullable String name) {
        super(ItemID.STRING_IDENTIFIED_ITEM, 0, 1, StringItem.notEmpty(name));
        this.id = id;
        this.textureName = name;
    }

    public ItemCustom(@Nonnull String id, @Nullable String name, @Nonnull String textureName) {
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

    /**
     * 该方法设置自定义物品的定义
     * <p>
     * This method sets the definition of custom item
     */
    public abstract CustomItemDefinition getDefinition();

    @Override
    public final int getId() {
        return super.getId();
    }

    @Override
    public ItemCustom clone() {
        return (ItemCustom) super.clone();
    }
}
