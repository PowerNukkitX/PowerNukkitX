package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.event.block.BlockFadeEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;

import java.util.concurrent.ThreadLocalRandom;


public abstract class BlockCoralBlock extends BlockSolid {
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(1.5)
            .resistance(6.0)
            .toolType(ItemTool.TYPE_PICKAXE)
            .build();
    public BlockCoralBlock(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockCoralBlock(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    public boolean isDead() {
        return false;
    }

    public BlockCoralBlock toDead() {
        return this;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isDead()) {
                this.getLevel().scheduleUpdate(this, 60 + ThreadLocalRandom.current().nextInt(40));
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!isDead()) {
                for (BlockFace face : BlockFace.values()) {
                    if (getSideAtLayer(0, face) instanceof BlockFlowingWater || getSideAtLayer(1, face) instanceof BlockFlowingWater
                            || getSideAtLayer(0, face) instanceof BlockFrostedIce || getSideAtLayer(1, face) instanceof BlockFrostedIce) {
                        return type;
                    }
                }
                BlockFadeEvent event = new BlockFadeEvent(this, toDead());
                if (!event.isCancelled()) {
                    this.getLevel().setBlock(this, event.getNewState(), true, true);
                }
            }
            return type;
        }
        return 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            if (item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null) {
                return new Item[]{toItem()};
            } else {
                return new Item[]{toDead().toItem()};
            }
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this);
    }

}
