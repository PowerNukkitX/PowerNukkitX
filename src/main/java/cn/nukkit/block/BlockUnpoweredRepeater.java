package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockUnpoweredRepeater extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:unpowered_repeater", CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.REPEATER_DELAY);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockUnpoweredRepeater() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockUnpoweredRepeater(BlockState blockstate) {
        super(blockstate);
    }
}