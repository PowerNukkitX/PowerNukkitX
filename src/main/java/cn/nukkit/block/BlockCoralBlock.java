package cn.nukkit.block;

import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.property.CommonBlockProperties.CORAL_COLOR;


public abstract class BlockCoralBlock extends BlockSolid {
    public BlockCoralBlock(BlockState blockstate) {
        super(blockstate);
    }

    public boolean isDead() {
        return false;
    }

    public BlockCoralBlock toDead() {
        return this;
    }

    @Override
    public double getHardness() {
        return 7;
    }

    @Override
    public double getResistance() {
        return 6.0;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
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
                return new Item[]{new ItemBlock(clone(), this.getPropertyValue(CORAL_COLOR).ordinal())};//0 - 4
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
