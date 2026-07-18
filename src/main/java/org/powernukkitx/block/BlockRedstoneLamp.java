package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.event.redstone.RedstoneUpdateEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author Nukkit Project Team
 */

public class BlockRedstoneLamp extends BlockSolid implements RedstoneComponent {

    public static final BlockProperties PROPERTIES = new BlockProperties(REDSTONE_LAMP);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedstoneLamp() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedstoneLamp(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Redstone Lamp";
    }

    @Override
    public double getHardness() {
        return 0.3D;
    }

    @Override
    public double getResistance() {
        return 1.5D;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (this.isGettingPower()) {
            this.level.setBlock(this, Block.get(BlockID.LIT_REDSTONE_LAMP), false, true);
        } else {
            this.level.setBlock(this, this, false, true);
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
            if (!this.level.getServer().getSettings().gameplaySettings().enableRedstone()) {
                return 0;
            }

            if (this.isGettingPower()) {
                // Redstone event
                RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
                getLevel().getServer().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return 0;
                }

                this.level.updateComparatorOutputLevelSelective(this, true);

                this.level.setBlock(this, Block.get(BlockID.LIT_REDSTONE_LAMP), false, false);
                return 1;
            }
        }

        return 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                new ItemBlock(Block.get(BlockID.REDSTONE_LAMP))
        };
    }
}
