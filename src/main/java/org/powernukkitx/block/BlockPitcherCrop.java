package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.particle.BoneMealParticle;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.NukkitMath;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class BlockPitcherCrop extends BlockCrops {

    public static final BlockProperties PROPERTIES =
            new BlockProperties(PITCHER_CROP, CommonBlockProperties.GROWTH, CommonBlockProperties.UPPER_BLOCK_BIT);

    public BlockPitcherCrop() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPitcherCrop(BlockState state) {
        super(state);
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Pitcher Crop";
    }

    public boolean isUpper() {
        return this.getPropertyValue(CommonBlockProperties.UPPER_BLOCK_BIT);
    }

    private static final int[] AGE_TO_GROWTH = {0, 1, 3, 5, 7};

    private int getLogicalAge() {
        int growth = getGrowth();
        if (growth >= 7) return 4;
        if (growth >= 5) return 3;
        if (growth >= 3) return 2;
        if (growth >= 1) return 1;
        return 0;
    }

    private void setLogicalAge(int stage) {
        setGrowth(AGE_TO_GROWTH[NukkitMath.clamp(stage, 0, 4)]);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (!isUpper()) {
                int stage = getLogicalAge();

                if (stage < 4 && ThreadLocalRandom.current().nextInt(5) == 0) {
                    int newStage = stage + 1;
                    setLogicalAge(newStage);
                    setPropertyValue(CommonBlockProperties.UPPER_BLOCK_BIT, false);
                    level.setBlock(getPosition(), this, true, true);

                    if (newStage >= 2) {
                        updateUpperBlock(newStage);
                    }
                }
            }
            return type;
        }
        return 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{Item.get(ItemID.PITCHER_POD)};
    }

    @Override
    public boolean onBreak(Item item) {
        if (!isUpper()) {
            Block above = up();
            if (above instanceof BlockPitcherCrop upper && upper.isUpper()) {
                level.setBlock(above.getPosition(), Block.get(AIR), true, true);
            }

            return super.onBreak(item);
        }

        Block below = down();
        if (below instanceof BlockPitcherCrop lower && !lower.isUpper()) {
            level.setBlock(below.getPosition(), Block.get(AIR), true, true);
        }

        return super.onBreak(item);
    }


    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (!item.isFertilizer()) return false;

        BlockPitcherCrop lower = this;
        if (isUpper()) {
            Block below = down();
            if (!(below instanceof BlockPitcherCrop crop) || crop.isUpper()) {
                return false;
            }
            lower = crop;
        }

        int stage = lower.getLogicalAge();

        if (stage >= 4) {
            return false;
        }

        int newStage = stage + 1;
        lower.setLogicalAge(newStage);
        lower.setPropertyValue(CommonBlockProperties.UPPER_BLOCK_BIT, false);
        level.setBlock(lower.getPosition(), lower, true, true);

        Block above = lower.up();

        if (newStage >= 2) {
            lower.updateUpperBlock(newStage);
        } else {
            if (above instanceof BlockPitcherCrop upper && upper.isUpper()) {
                level.setBlock(above.getPosition(), Block.get(AIR), true, true);
            }
        }

        if (player != null && !player.isCreative()) {
            item.count--;
        }

        this.level.addParticle(new BoneMealParticle(lower));

        return true;
    }

    private void updateUpperBlock(int newStage) {
        Block above = this.up();

        if (above.isAir()) {
            BlockPitcherCrop upper = new BlockPitcherCrop();
            upper.setPropertyValue(CommonBlockProperties.UPPER_BLOCK_BIT, true);
            upper.setLogicalAge(newStage);
            level.setBlock(above.getPosition(), upper, true, true);
            return;
        }

        if (above instanceof BlockPitcherCrop upper && upper.isUpper()) {
            upper.setLogicalAge(newStage);
            level.setBlock(above.getPosition(), upper, true, true);
        }
    }
}
