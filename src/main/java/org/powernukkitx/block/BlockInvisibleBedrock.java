package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockInvisibleBedrock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(INVISIBLE_BEDROCK);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(-1)
            .resistance(18000000)
            .canBePushed(false)
            .canBePulled(false)
            .canBeFlowedInto(false)
            .waterloggingLevel(2)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockInvisibleBedrock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockInvisibleBedrock(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Invisible Bedrock";
    }

    @Override
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.AIR));
    }
}