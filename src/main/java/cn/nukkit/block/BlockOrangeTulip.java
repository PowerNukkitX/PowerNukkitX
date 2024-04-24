package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOrangeTulip extends BlockFlower {
     public static final BlockProperties PROPERTIES = new BlockProperties(ORANGE_TULIP);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockOrangeTulip() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockOrangeTulip(BlockState blockstate) {
         super(blockstate);
     }
}