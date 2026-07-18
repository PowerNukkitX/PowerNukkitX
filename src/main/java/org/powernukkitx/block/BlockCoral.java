package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.event.block.BlockFadeEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

import static org.powernukkitx.block.property.CommonBlockProperties.LIQUID_DEPTH;


public abstract class BlockCoral extends BlockFlowable {
    public static final BlockDefinition DEFINITION = FLOWABLE.toBuilder()
            .build();
    public static final int TYPE_TUBE = 0;
    public static final int TYPE_BRAIN = 1;
    public static final int TYPE_BUBBLE = 2;
    public static final int TYPE_FIRE = 3;
    public static final int TYPE_HORN = 4;

    public BlockCoral(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockCoral(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    public abstract boolean isDead();

    public abstract Block getDeadCoral();

    public void setDead(Block deadBlock) {
        this.getLevel().setBlock(this, deadBlock, true, true);
    }

    @Override
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block down = down();
            if (!down.isSolid()) {
                this.getLevel().useBreakOn(this);
            } else if (!isDead()) {
                this.getLevel().scheduleUpdate(this, 60 + ThreadLocalRandom.current().nextInt(40));
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!isDead() && !(getLevelBlockAtLayer(1) instanceof BlockFlowingWater) && !(getLevelBlockAtLayer(1) instanceof BlockFrostedIce)) {
                BlockFadeEvent event = new BlockFadeEvent(this, getDeadCoral());
                if (!event.isCancelled()) {
                    setDead(event.getNewState());
                }
            }
            return type;
        }
        return 0;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = down();
        Block layer1 = block.getLevelBlockAtLayer(1);
        boolean hasWater = layer1 instanceof BlockFlowingWater;
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
    public Item[] getDrops(Item item) {
        if (item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null) {
            return super.getDrops(item);
        } else {
            return Item.EMPTY_ARRAY;
        }
    }
}
