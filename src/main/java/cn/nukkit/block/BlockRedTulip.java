package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockRedTulip extends BlockFlower {
     public static final BlockProperties $1 = new BlockProperties(RED_TULIP);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

    public BlockRedTulip() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

     public BlockRedTulip(BlockState blockstate) {
         super(blockstate);
     }
}