package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPoweredRepeater extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:powered_repeater", CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.REPEATER_DELAY);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPoweredRepeater() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPoweredRepeater(BlockState blockstate) {
        super(blockstate);
    }
}