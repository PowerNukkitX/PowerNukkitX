package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.IN_WALL_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.OPEN_BIT;

public class BlockJungleFenceGate extends BlockFenceGate {
    public static final BlockProperties PROPERTIES = new BlockProperties(JUNGLE_FENCE_GATE,  IN_WALL_BIT, MINECRAFT_CARDINAL_DIRECTION, OPEN_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockJungleFenceGate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockJungleFenceGate(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Jungle Fence Gate";
    }
}