package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPoweredComparator extends BlockRedstoneComparator {
    public static final BlockProperties PROPERTIES = new BlockProperties(POWERED_COMPARATOR, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.OUTPUT_LIT_BIT, CommonBlockProperties.OUTPUT_SUBTRACT_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPoweredComparator() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPoweredComparator(BlockState blockstate) {
        super(blockstate);
        this.isPowered = true;
    }

    @Override
    public String getName() {
        return "Comparator Block Powered";
    }

    @Override
    public BlockRedstoneComparator getPowered() {
        return this;
    }
}