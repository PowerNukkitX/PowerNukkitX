package cn.nukkit.education.block.glass;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockHardLightGrayStainedGlass extends Block {
     public static final BlockProperties PROPERTIES = new BlockProperties(HARD_LIGHT_GRAY_STAINED_GLASS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockHardLightGrayStainedGlass(BlockState blockstate) {
         super(blockstate);
     }
}