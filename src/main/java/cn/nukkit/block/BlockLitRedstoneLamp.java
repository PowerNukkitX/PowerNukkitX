package cn.nukkit.block;

import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import org.jetbrains.annotations.NotNull;

public class BlockLitRedstoneLamp extends BlockRedstoneLamp {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIT_REDSTONE_LAMP);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLitRedstoneLamp() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLitRedstoneLamp(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Lit Redstone Lamp";
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.REDSTONE_LAMP));
    }

    @Override
    public int onUpdate(int type) {
        if (!this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            return 0;
        }

        if ((type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) && !this.isGettingPower()) {
            this.level.scheduleUpdate(this, 4);
            return 1;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED && !this.isGettingPower()) {
            // Redstone event
            RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
            this.level.getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return 0;
            }

            this.level.updateComparatorOutputLevelSelective(this, true);

            this.level.setBlock(this, Block.get(BlockID.REDSTONE_LAMP), false, false);
        }
        return 0;
    }
}