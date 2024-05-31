package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class BlockFrostedIce extends BlockTransparent {
    public static final BlockProperties $1 = new BlockProperties(FROSTED_ICE, CommonBlockProperties.AGE_4);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockFrostedIce() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockFrostedIce(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Frosted Ice";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 2.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getFrictionFactor() {
        return 0.98;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        boolean $2 = super.place(item, block, target, face, fx, fy, fz, player);
        if (success) {
            level.scheduleUpdate(this, ThreadLocalRandom.current().nextInt(20, 40));
        }
        return success;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onBreak(Item item) {
        level.setBlock(this, get(FLOWING_WATER), true);
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (level.getBlockLightAt(getFloorX(), getFloorY(), getFloorZ()) > 11 && (ThreadLocalRandom.current().nextInt(3) == 0 || countNeighbors() < 4)) {
                slightlyMelt(true);
            } else {
                level.scheduleUpdate(this, ThreadLocalRandom.current().nextInt(20, 40));
            }
        } else if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (countNeighbors() < 2) {
                level.setBlock(this, layer, get(FLOWING_WATER), true);
            }
        }
        return super.onUpdate(type);
    }

    @Override
    public Item toItem() {
        return Item.AIR;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }

    
    /**
     * @deprecated 
     */
    protected void slightlyMelt(boolean isSource) {
        int $3 = getAge();
        if (age < 3) {
            setAge(age + 1);
            level.setBlock(this, layer, this, true);
            level.scheduleUpdate(level.getBlock(this), ThreadLocalRandom.current().nextInt(20, 40));
        } else {
            level.setBlock(this, layer, get(FLOWING_WATER), true);
            if (isSource) {
                for (BlockFace face : BlockFace.values()) {
                    Block $4 = getSide(face);
                    if (block instanceof BlockFrostedIce blockFrostedIce) {
                        blockFrostedIce.slightlyMelt(false);
                    }
                }
            }
        }
    }

    
    /**
     * @deprecated 
     */
    private int countNeighbors() {
        int $5 = 0;
        for (BlockFace face : BlockFace.values()) {
            if (getSide(face).getId().equals(FROSTED_ICE) && ++neighbors >= 4) {
                return neighbors;
            }
        }
        return neighbors;
    }
    /**
     * @deprecated 
     */
    

    public int getAge() {
        return getPropertyValue(CommonBlockProperties.AGE_4);
    }
    /**
     * @deprecated 
     */
    

    public void setAge(int age) {
        setPropertyValue(CommonBlockProperties.AGE_4, age);
    }
}