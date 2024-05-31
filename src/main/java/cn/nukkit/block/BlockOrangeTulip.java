package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOrangeTulip extends BlockFlower {
     public static final BlockProperties $1 = new BlockProperties(ORANGE_TULIP);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

    public BlockOrangeTulip() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

     public BlockOrangeTulip(BlockState blockstate) {
         super(blockstate);
     }
}