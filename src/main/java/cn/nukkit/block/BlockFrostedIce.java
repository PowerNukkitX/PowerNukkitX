package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class BlockFrostedIce extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(FROSTED_ICE, CommonBlockProperties.AGE_4);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockFrostedIce() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockFrostedIce(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Frosted Ice";
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getFrictionFactor() {
        return 0.98;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        boolean success = super.place(item, block, target, face, fx, fy, fz, player);
        if (success) {
            level.scheduleUpdate(this, ThreadLocalRandom.current().nextInt(20, 40));
        }
        return success;
    }

    @Override
    public boolean onBreak(Item item) {
        level.setBlock(this, get(FLOWING_WATER), true);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (level.getFullLight(this) > 11 && (ThreadLocalRandom.current().nextInt(3) == 0 || countNeighbors() < 4)) {
                slightlyMelt(true);
            } else {
                level.scheduleUpdate(this, ThreadLocalRandom.current().nextInt(20, 40));
            }
        } else if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (countNeighbors() < 2) {
                level.setBlock(this, layer, get(WATER), true);
            }
        }
        return super.onUpdate(type);
    }

    @Override
    public Item toItem() {
        return Item.AIR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    protected void slightlyMelt(boolean isSource) {
        int age = getAge();
        if (age < 3) {
            setAge(age + 1);
            level.setBlock(this, layer, this, true);
            level.scheduleUpdate(level.getBlock(this), ThreadLocalRandom.current().nextInt(20, 40));
        } else {
            level.setBlock(this, layer, get(WATER), true);
            if (isSource) {
                for (BlockFace face : BlockFace.values()) {
                    Block block = getSide(face);
                    if (block instanceof BlockFrostedIce blockFrostedIce) {
                        blockFrostedIce.slightlyMelt(false);
                    }
                }
            }
        }
    }

    private int countNeighbors() {
        int neighbors = 0;
        for (BlockFace face : BlockFace.values()) {
            if (getSide(face).getId().equals(FROSTED_ICE) && ++neighbors >= 4) {
                return neighbors;
            }
        }
        return neighbors;
    }

    public int getAge() {
        return getPropertyValue(CommonBlockProperties.AGE_4);
    }

    public void setAge(int age) {
        setPropertyValue(CommonBlockProperties.AGE_4, age);
    }
}