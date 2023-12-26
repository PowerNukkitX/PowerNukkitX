package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSoulCampfire extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:soul_campfire", CommonBlockProperties.EXTINGUISHED, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSoulCampfire() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSoulCampfire(BlockState blockstate) {
        super(blockstate);
    }
}