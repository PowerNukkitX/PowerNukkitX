package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemPitcherPod;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
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

    private int getLogicalAge() {
        int g = getGrowth();
        if (g <= 0) return 0;
        if (g == 1) return 1;
        if (g == 2) return 2;
        if (g == 3) return 3;
        return 4;
    }

    private void setLogicalAge(int stage) {
        int g = switch (stage) {
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 3;
            case 4 -> 4;
            default -> 0;
        };
        setGrowth(g);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (!isUpper()) {
                int stage = getLogicalAge();

                if (stage < 4) {
                    if (ThreadLocalRandom.current().nextInt(5) == 0) {
                        int newStage = stage + 1;
                        setLogicalAge(newStage);
                        setPropertyValue(CommonBlockProperties.UPPER_BLOCK_BIT, false);
                        level.setBlock(getPosition(), this, true, true);

                        if (newStage >= 3) {
                            updateUpperBlock(newStage);
                        }
                    }
                }
            }
            return type;
        }
        return 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    public boolean onBreak(Item item) {
        int stage = getLogicalAge();

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

        if (stage < 4) {
            this.level.dropItem(this, new ItemPitcherPod());
        } else {
            this.level.dropItem(this, Item.get(PITCHER_PLANT, 0, 1));
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
            updateUpperBlock(newStage);
        } else {
            if (above instanceof BlockPitcherCrop upper && upper.isUpper()) {
                level.setBlock(above.getPosition(), Block.get(AIR), true, true);
            }
        }

        if (player != null && (player.gamemode & 0x01) == 0) {
            item.count--;
        }

        this.level.addParticle(new BoneMealParticle(lower));

        return true;
    }

    private void updateUpperBlock(int newStage) {
        Block above = this.up();

        if (above.getId().equals(AIR)) {
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