package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.property.CommonBlockProperties.LIQUID_DEPTH;


public abstract class BlockCoral extends BlockFlowable {
    public static final int $1 = 0;
    public static final int $2 = 1;
    public static final int $3 = 2;
    public static final int $4 = 3;
    public static final int $5 = 4;
    /**
     * @deprecated 
     */
    

    public BlockCoral(BlockState blockstate) {
        super(blockstate);
    }

    public abstract boolean isDead();

    public abstract Block getDeadCoral();
    /**
     * @deprecated 
     */
    

    public void setDead(Block deadBlock) {
        this.getLevel().setBlock(this, deadBlock, true, true);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block $6 = down();
            if (!down.isSolid()) {
                this.getLevel().useBreakOn(this);
            } else if (!isDead()) {
                this.getLevel().scheduleUpdate(this, 60 + ThreadLocalRandom.current().nextInt(40));
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!isDead() && !(getLevelBlockAtLayer(1) instanceof BlockFlowingWater) && !(getLevelBlockAtLayer(1) instanceof BlockFrostedIce)) {
                BlockFadeEvent $7 = new BlockFadeEvent(this, getDeadCoral());
                if (!event.isCancelled()) {
                    setDead(event.getNewState());
                }
            }
            return type;
        }
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        Block $8 = down();
        Block $9 = block.getLevelBlockAtLayer(1);
        boolean $10 = layer1 instanceof BlockFlowingWater;
        int waterDamage;
        if (!layer1.isAir() && (!hasWater || ((waterDamage = layer1.getPropertyValue(LIQUID_DEPTH)) != 0) && waterDamage != 8)) {
            return false;
        }

        if (hasWater && layer1.getPropertyValue(LIQUID_DEPTH) == 8) {
            this.getLevel().setBlock(this, 1, new BlockFlowingWater(), true, false);
        }

        if (down.isSolid()) {
            this.getLevel().setBlock(this, 0, this, true, true);
            return true;
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null) {
            return super.getDrops(item);
        } else {
            return Item.EMPTY_ARRAY;
        }
    }
}
