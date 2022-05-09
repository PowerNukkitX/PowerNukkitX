package cn.nukkit.item.customitem;

import cn.nukkit.block.BlockCustom;
import org.jetbrains.annotations.NotNull;

public class ItemCustomBlock extends ItemCustom {

    public ItemCustomBlock(@NotNull BlockCustom block) {
        super(block.getNamespace(), block.getName());
        this.block = block;
    }
}
