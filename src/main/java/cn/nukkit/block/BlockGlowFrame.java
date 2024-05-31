package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class BlockGlowFrame extends BlockFrame {
    public static final BlockProperties $1 = new BlockProperties(GLOW_FRAME, CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.ITEM_FRAME_MAP_BIT, CommonBlockProperties.ITEM_FRAME_PHOTO_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockGlowFrame() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockGlowFrame(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Glow Item Frame";
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getBlockEntityType() {
        return BlockEntity.GLOW_ITEM_FRAME;
    }
}