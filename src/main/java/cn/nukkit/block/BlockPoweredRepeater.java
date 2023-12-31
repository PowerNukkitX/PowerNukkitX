package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPoweredRepeater extends BlockRedstoneRepeater {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:powered_repeater",
            CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION,
            CommonBlockProperties.REPEATER_DELAY);

    @Override
    public @NotNull BlockProperties getProperties() {
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
    protected Block getPowered() {
        return this;
    }

    @Override
    protected Block getUnpowered() {
        BlockUnpoweredRepeater blockUnpoweredRepeater = new BlockUnpoweredRepeater();
        return blockUnpoweredRepeater.setPropertyValues(blockstate.getBlockPropertyValues());
    }

    @Override
    public int getLightLevel() {
        return 7;
    }
}