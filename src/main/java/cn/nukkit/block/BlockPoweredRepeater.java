package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPoweredRepeater extends BlockRedstoneRepeater {
    public static final BlockProperties PROPERTIES = new BlockProperties(POWERED_REPEATER, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.REPEATER_DELAY);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPoweredRepeater() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPoweredRepeater(BlockState blockstate) {
        super(blockstate);
        isPowered = true;
    }

    @Override
    public String getName() {
        return "Powered Repeater";
    }

    @Override
    public Block getPowered() {
        return this;
    }

    @Override
    public Block getUnpowered() {
        BlockUnpoweredRepeater blockUnpoweredRepeater = new BlockUnpoweredRepeater();
        return blockUnpoweredRepeater.setPropertyValues(blockstate.getBlockPropertyValues());
    }

    @Override
    public int getLightLevel() {
        return 7;
    }
}