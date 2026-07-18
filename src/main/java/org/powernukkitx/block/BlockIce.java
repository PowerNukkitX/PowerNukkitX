package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.event.block.BlockFadeEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockIce extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(ICE);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(0.5)
            .resistance(2.5)
            .toolType(ItemTool.TYPE_PICKAXE)
            .burnChance(-1)
            .build();

    public BlockIce() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockIce(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Ice";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getFrictionFactor() {
        return 0.98;
    }

    
    @Override
    public boolean onBreak(Item item) {
        if (level.getDimension() == Level.DIMENSION_NETHER 
                || item.getEnchantmentLevel(Enchantment.ID_SILK_TOUCH) > 0 
                || down().isAir()) {
            return super.onBreak(item);
        }
        
        return level.setBlock(this, Block.get(BlockID.WATER), true);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (level.getBlockLightAt((int) this.x, (int) this.y, (int) this.z) >= 12) {
                BlockFadeEvent event = new BlockFadeEvent(this, level.getDimension() == Level.DIMENSION_NETHER ? get(AIR) : get(WATER));
                level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    level.setBlock(this, event.getNewState(), true);
                }
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }
        return 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    
    @Override
    public int getLightFilter() {
        return 2;
    }
}
