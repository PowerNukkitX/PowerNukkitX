package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockSuspiciousGravel extends BlockBrushable {

    public static final BlockProperties PROPERTIES = new BlockProperties(SUSPICIOUS_GRAVEL, CommonBlockProperties.HANGING, CommonBlockProperties.BRUSHED_PROGRESS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSuspiciousGravel() {
        this(PROPERTIES.getDefaultState());
    }
    public BlockSuspiciousGravel(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        return BlockEntityHolder.setBlockAndCreateEntity(this, false, true) != null;
    }

    public String getName() {
        return "Suspicious Gravel";
    }

    @Override
    public double getHardness() {
        return 0.25;
    }

    @Override
    public double getResistance() {
        return 1.25;
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