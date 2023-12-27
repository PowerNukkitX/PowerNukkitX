package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLitSmoker extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lit_smoker", CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLitSmoker() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLitSmoker(BlockState blockstate) {
        super(blockstate);
    }
}