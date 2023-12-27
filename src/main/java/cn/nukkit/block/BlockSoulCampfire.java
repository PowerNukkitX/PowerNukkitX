package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
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