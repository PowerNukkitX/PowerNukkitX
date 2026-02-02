package cn.nukkit.block;

import cn.nukkit.block.definition.BlockDefinition;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockNetherBrickFence extends BlockFenceNonFlammable {
    public static final BlockProperties PROPERTIES = new BlockProperties(NETHER_BRICK_FENCE);
    public static final BlockDefinition DEFINITION = BlockFenceNonFlammable.DEFINITION.toBuilder()
            .hardness(2)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockNetherBrickFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockNetherBrickFence(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Nether Brick Fence";
    }
}