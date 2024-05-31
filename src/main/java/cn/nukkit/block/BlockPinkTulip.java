package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPinkTulip extends BlockFlower {
     public static final BlockProperties $1 = new BlockProperties(PINK_TULIP);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

    public BlockPinkTulip() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

     public BlockPinkTulip(BlockState blockstate) {
         super(blockstate);
     }
}