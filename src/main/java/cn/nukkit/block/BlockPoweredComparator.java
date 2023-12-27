package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPoweredComparator extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:powered_comparator", CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.OUTPUT_LIT_BIT, CommonBlockProperties.OUTPUT_SUBTRACT_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPoweredComparator() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPoweredComparator(BlockState blockstate) {
        super(blockstate);
    }
}