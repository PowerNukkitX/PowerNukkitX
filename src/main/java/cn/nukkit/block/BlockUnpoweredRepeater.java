package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockUnpoweredRepeater extends BlockRedstoneRepeater {
    public static final BlockProperties $1 = new BlockProperties(UNPOWERED_REPEATER, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.REPEATER_DELAY);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockUnpoweredRepeater() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockUnpoweredRepeater(BlockState blockstate) {
        super(blockstate);
        this.isPowered = false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Unpowered Repeater";
    }

    @Override
    public Block getPowered() {
        return new BlockPoweredRepeater().setPropertyValues(blockstate.getBlockPropertyValues());
    }

    @Override
    public Block getUnpowered() {
        return this;
    }
}