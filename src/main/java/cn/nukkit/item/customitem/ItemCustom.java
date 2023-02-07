package cn.nukkit.item.customitem;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.StringItem;
import cn.nukkit.item.StringItemBase;

import org.jetbrains.annotations.NotNull;

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
public abstract class ItemCustom extends StringItemBase implements CustomItem {
    private final String textureName;

    public ItemCustom(@NotNull String id, @Nullable String name) {
        super(id, StringItem.notEmpty(name));
        this.textureName = name;
    }

    public ItemCustom(@NotNull String id, @Nullable String name, @NotNull String textureName) {
        super(id, StringItem.notEmpty(name));
        this.textureName = textureName;
    }

    public String getTextureName() {
        return textureName;
    }

    /**
     * 该方法设置自定义物品的定义
     * <p>
     * This method sets the definition of custom item
     */
    public abstract CustomItemDefinition getDefinition();

    @Override
    public ItemCustom clone() {
        return (ItemCustom) super.clone();
    }
}
