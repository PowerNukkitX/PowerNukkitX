package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPoweredRepeater extends BlockRedstoneRepeater {
    public static final BlockProperties $1 = new BlockProperties(POWERED_REPEATER, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.REPEATER_DELAY);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPoweredRepeater() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPoweredRepeater(BlockState blockstate) {
        super(blockstate);
        isPowered = true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Powered Repeater";
    }

    @Override
    public Block getPowered() {
        return this;
    }

    @Override
    public Block getUnpowered() {
        BlockUnpoweredRepeater $2 = new BlockUnpoweredRepeater();
        return blockUnpoweredRepeater.setPropertyValues(blockstate.getBlockPropertyValues());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightLevel() {
        return 7;
    }
}