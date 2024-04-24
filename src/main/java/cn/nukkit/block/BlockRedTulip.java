package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockRedTulip extends BlockFlower {
     public static final BlockProperties PROPERTIES = new BlockProperties(RED_TULIP);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockRedTulip() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockRedTulip(BlockState blockstate) {
         super(blockstate);
     }
}