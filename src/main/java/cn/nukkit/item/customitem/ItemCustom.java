package cn.nukkit.item.customitem;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.item.customitem.CustomItemDefinition.BlockPlacerData;

import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;


/**
 * Inherit this class to implement a custom item, override the methods in the {@link Item} to control the feature of the item.
 *
 * @author lt_name
 */
public abstract class ItemCustom extends Item implements CustomItem {
    public ItemCustom(@NotNull String id) {
        super(id);
    }

    /**
     * This method sets the definition of custom item
     */
    public abstract CustomItemDefinition getDefinition();

    @Override
    public ItemCustom clone() {
        return (ItemCustom) super.clone();
    }

    @Nullable
    public Block getBlockPlacerTargetBlock() {
        BlockPlacerData data = this.getDefinition().getBlockPlacerData();
        return data != null ? Block.get(data.blockId()) : null;
    }
}
