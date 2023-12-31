package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockUnpoweredRepeater extends BlockRedstoneRepeater {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:unpowered_repeater",
            CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION,
            CommonBlockProperties.REPEATER_DELAY);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockUnpoweredRepeater() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockUnpoweredRepeater(BlockState blockstate) {
        super(blockstate);
        this.isPowered = false;
    }

    @Override
    public String getName() {
        return "Unpowered Repeater";
    }

    @Override
    protected Block getPowered() {
        return new BlockPoweredRepeater().setPropertyValues(blockstate.getBlockPropertyValues());
    }

    @Override
    protected Block getUnpowered() {
        return this;
    }
}