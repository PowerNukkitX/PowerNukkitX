package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLitPumpkin extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lit_pumpkin", CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLitPumpkin() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLitPumpkin(BlockState blockstate) {
        super(blockstate);
    }
}