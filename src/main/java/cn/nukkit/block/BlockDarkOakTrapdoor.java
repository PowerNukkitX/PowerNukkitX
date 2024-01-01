package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDarkOakTrapdoor extends BlockTrapdoor {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:dark_oak_trapdoor", CommonBlockProperties.DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPSIDE_DOWN_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDarkOakTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDarkOakTrapdoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Dark Oak Trapdoor";
    }
}