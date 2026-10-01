package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockSuspiciousGravel extends BlockBrushable {

    public static final BlockProperties PROPERTIES = new BlockProperties(SUSPICIOUS_GRAVEL, CommonBlockProperties.HANGING, CommonBlockProperties.BRUSHED_PROGRESS);
    public static final BlockDefinition DEFINITION = BlockBrushable.DEFINITION.toBuilder()
            .hardness(0.25)
            .resistance(1.25)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSuspiciousGravel() {
        this(PROPERTIES.getDefaultState());
    }
    public BlockSuspiciousGravel(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        return BlockEntityHolder.setBlockAndCreateEntity(this, false, true) != null;
    }

    public String getName() {
        return "Suspicious Gravel";
    }

    @Override
    public Block getFinalState() {
        return BlockGravel.PROPERTIES.getDefaultState().toBlock();
    }

    @Override
    protected Sound getHitSound() {
        return Sound.HIT_SUSPICIOUS_GRAVEL;
    }

    @Override
    protected Sound getBreakSound() {
        return Sound.BREAK_SUSPICIOUS_GRAVEL;
    }
}