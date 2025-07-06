package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDriedGhast extends BlockTransparent {

    public static final BlockProperties PROPERTIES = new BlockProperties(DRIED_GHAST, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.REHYDRATION_LEVEL);

    public BlockDriedGhast(BlockState blockState) {
        super(blockState);
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Dried Ghast";
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }
}
