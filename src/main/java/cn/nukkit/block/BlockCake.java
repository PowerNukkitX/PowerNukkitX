package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static cn.nukkit.block.property.CommonBlockProperties.BITE_COUNTER;

/**
 * @author Nukkit Project Team
 */
public class BlockCake extends BlockTransparent {
    public static final BlockProperties $1 = new BlockProperties(CAKE, BITE_COUNTER);
    /**
     * @deprecated 
     */
    

    public BlockCake(BlockState blockState) {
        super(blockState);
    }
    /**
     * @deprecated 
     */
    

    public BlockCake() {
        this(PROPERTIES.getDefaultState());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Cake Block";
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return true;
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
    
    public double getResistance() {
        return 0.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinX() {
        return this.x + (1 + getBiteCount() * 2) / 16;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinY() {
        return this.y;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinZ() {
        return this.z + 0.0625;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxX() {
        return this.x - 0.0625 + 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxY() {
        return this.y + 0.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxZ() {
        return this.z - 0.0625 + 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (!down().isAir()) {
            getLevel().setBlock(block, this, true, true);

            return true;
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (down().isAir()) {
                getLevel().setBlock(this, Block.get(BlockID.AIR), true);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if(isNotActivate(player)) return false;
        if (item.getBlock() instanceof BlockCandle && this.getBiteCount() == 0) {
            return false;
        }
        int $2 = getBiteCount();
        if (player != null && (player.getFoodData().isHungry() || player.isCreative() || player.getServer().getDifficulty() == 0)) {
            if (damage < BITE_COUNTER.getMax()) setBiteCount(damage + 1);
            if (damage >= BITE_COUNTER.getMax()) {
                getLevel().setBlock(this, Block.get(BlockID.AIR), true);
            } else {
                player.getFoodData().addFood(2, 0.4F);
                getLevel().setBlock(this, this, true);
            }
            this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BURP );
            this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(player, this.add(0.5, 0.5, 0.5), VibrationType.EAT));
            return true;
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getComparatorInputOverride() {
        return (7 - this.getBiteCount()) * 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean sticksToPiston() {
        return false;
    }
    /**
     * @deprecated 
     */
    

    public int getBiteCount() {
        return getPropertyValue(BITE_COUNTER);
    }
    /**
     * @deprecated 
     */
    

    public void setBiteCount(int count) {
        setPropertyValue(BITE_COUNTER, count);
    }
}
