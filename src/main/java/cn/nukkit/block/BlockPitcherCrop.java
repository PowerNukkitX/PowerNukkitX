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

    // Raw Bedrock growth (0–7)
    public int getGrowth() {
        return this.getPropertyValue(CommonBlockProperties.GROWTH);
    }

    // Logical vanilla age (0–3)
    // 0–4, directly mirroring the five visual stages
    private int getLogicalAge() {
        int g = getGrowth();
        if (g <= 0) return 0;
        if (g == 1) return 1;
        if (g == 2) return 2;
        if (g == 3) return 3;
        return 4; // g >= 4
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

                if (stage < 4) { // 0–3 can grow
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
        int stage = getLogicalAge();

        // Bottom block NEVER drops anything in vanilla
        if (!isUpper()) {
            return Item.EMPTY_ARRAY;
        }

        // Upper block:
        // - If not fully grown → drop 1 pod
        // - If fully grown → drop 1 plant
        if (stage < 4) {
            return new Item[]{ new ItemPitcherPod() };
        }

        return new Item[]{ Item.get(PITCHER_PLANT, 0, 1) };
    }

    @Override
    public boolean onBreak(Item item) {
        // Always break the whole plant
        if (!isUpper()) {
            // Breaking bottom: remove upper if present
            Block above = up();
            if (above instanceof BlockPitcherCrop upper && upper.isUpper()) {
                level.setBlock(above.getPosition(), Block.get(AIR), true, true);
            }

            // Bottom block NEVER drops anything
            return super.onBreak(item);
        }

        // Breaking upper block
        // Remove lower block
        Block below = down();
        if (below instanceof BlockPitcherCrop lower && !lower.isUpper()) {
            level.setBlock(below.getPosition(), Block.get(AIR), true, true);
        }

        // DO NOT drop items here — getDrops() handles it
        return super.onBreak(item);
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (!item.isFertilizer()) return false;

        // Always apply bone meal to the LOWER block
        BlockPitcherCrop lower = this;
        if (isUpper()) {
            Block below = down();
            if (!(below instanceof BlockPitcherCrop crop) || crop.isUpper()) {
                return false; // invalid structure
            }
            lower = crop;
        }

        int stage = lower.getLogicalAge();

        // Already fully grown
        if (stage >= 4) {
            return false;
        }

        // Grow by 1 stage
        int newStage = stage + 1;
        lower.setLogicalAge(newStage);
        lower.setPropertyValue(CommonBlockProperties.UPPER_BLOCK_BIT, false);
        level.setBlock(lower.getPosition(), lower, true, true);

        // Handle upper block creation/update
        Block above = lower.up();

        if (newStage >= 2) {
            updateUpperBlock(newStage);
        } else {
            // Stage 0–1: ensure no upper block exists
            if (above instanceof BlockPitcherCrop upper && upper.isUpper()) {
                level.setBlock(above.getPosition(), Block.get(AIR), true, true);
            }
        }

        // Consume bone meal (unless creative)
        if (player != null && (player.gamemode & 0x01) == 0) {
            item.count--;
        }

        // Bone meal particles
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