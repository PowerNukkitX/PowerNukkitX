package cn.nukkit.block;

import cn.nukkit.level.Sound;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.BRUSHED_PROGRESS;
import static cn.nukkit.block.property.CommonBlockProperties.HANGING;


public class BlockSuspiciousSand extends BlockBrushable {
    public static final BlockProperties PROPERTIES = new BlockProperties(SUSPICIOUS_SAND, HANGING, BRUSHED_PROGRESS);

    public BlockSuspiciousSand() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSuspiciousSand(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public String getName() {
        return "Suspicious Sand";
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
        return BlockSand.PROPERTIES.getDefaultState().toBlock();
    }

    @Override
    protected Sound getHitSound() {
        return Sound.HIT_SUSPICIOUS_SAND;
    }

    @Override
    protected Sound getBreakSound() {
        return Sound.BREAK_SUSPICIOUS_SAND;
    }
}