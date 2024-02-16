package cn.nukkit.item.customitem;

import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;


/**
 * 继承这个类实现自定义物品,重写{@link Item}中的方法控制方块属性
 * <p>
 * Inherit this class to implement a custom item, override the methods in the {@link Item} to control the feature of the item.
 *
 * @author lt_name
 */
public abstract class ItemCustom extends Item implements CustomItem {
    public ItemCustom(@NotNull String id) {
        super(id);
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
