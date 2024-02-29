package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockUnpoweredComparator  extends BlockRedstoneComparator {
    public static final BlockProperties PROPERTIES = new BlockProperties(UNPOWERED_COMPARATOR, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.OUTPUT_LIT_BIT, CommonBlockProperties.OUTPUT_SUBTRACT_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockUnpoweredComparator() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockUnpoweredComparator(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Comparator Block Unpowered";
    }

    @Override
    public BlockRedstoneComparator getUnpowered() {
        return this;
    }
}