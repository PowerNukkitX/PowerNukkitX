package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemItemFrameGlow;
import org.jetbrains.annotations.NotNull;

/**
 * @author LoboMetalurgico
 * @since 13/06/2021
 */
public class BlockItemFrameGlow extends BlockItemFrame {


    public BlockItemFrameGlow() {
        this(0);
    }


    public BlockItemFrameGlow(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Glow Item Frame";
    }

    @Override
    public int getId() {
        return GLOW_FRAME;
    }


    @NotNull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.GLOW_ITEM_FRAME;
    }

    @Override
    public Item toItem() {
        return new ItemItemFrameGlow();
    }
}
