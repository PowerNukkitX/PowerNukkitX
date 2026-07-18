package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.event.block.BlockRedstoneEvent;
import org.powernukkitx.event.redstone.RedstoneUpdateEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.plugin.PluginManager;
import org.powernukkitx.utils.Faceable;
import org.powernukkitx.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static org.powernukkitx.block.property.CommonBlockProperties.MINECRAFT_FACING_DIRECTION;
import static org.powernukkitx.block.property.CommonBlockProperties.POWERED_BIT;

/**
 * @author Leonidius20, joserobjr
 * @since 18.08.18
 */
public class BlockObserver extends BlockSolid implements RedstoneComponent, Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(OBSERVER, CommonBlockProperties.MINECRAFT_FACING_DIRECTION, POWERED_BIT);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(3.5)
            .resistance(17.5)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .isPowerSource(true)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockObserver() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockObserver(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Observer";
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (player != null) {
            if (Math.abs(player.getFloorX() - this.x) <= 1 && Math.abs(player.getFloorZ() - this.z) <= 1) {
                double y = player.y + player.getEyeHeight();
                if (y - this.y > 2) {
                    setBlockFace(BlockFace.DOWN);
                } else if (this.y - y > 0) {
                    setBlockFace(BlockFace.UP);
                } else {
                    setBlockFace(player.getHorizontalFacing());
                }
            } else {
                setBlockFace(player.getHorizontalFacing());
            }
        }

        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    
    @Override
    public int getStrongPower(BlockFace side) {
        return isPowered() && side == getBlockFace() ? 15 : 0;
    }

    @Override
    public int getWeakPower(BlockFace face) {
        return getStrongPower(face);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED || type == Level.BLOCK_UPDATE_MOVED) {
            RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
            PluginManager pluginManager = level.getServer().getPluginManager();
            pluginManager.callEvent(ev);
            if (ev.isCancelled()) {
                return 0;
            }

            if (!isPowered()) {
                level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 0, 15));
                setPowered(true);

                if (level.setBlock(this, this)) {
                    getSide(getBlockFace().getOpposite()).onUpdate(Level.BLOCK_UPDATE_REDSTONE);
                    RedstoneComponent.updateAroundRedstone(getSide(getBlockFace().getOpposite()));
                    level.scheduleUpdate(this, 2);
                }
            } else {
                pluginManager.callEvent(new BlockRedstoneEvent(this, 15, 0));
                setPowered(false);

                level.setBlock(this, this);
                getSide(getBlockFace().getOpposite()).onUpdate(Level.BLOCK_UPDATE_REDSTONE);
                RedstoneComponent.updateAroundRedstone(getSide(getBlockFace().getOpposite()));
            }
            return type;
        }
        return 0;
    }

    @Override
    public void onNeighborChange(@NotNull BlockFace side) {
        Server server = level.getServer();
        BlockFace blockFace = getBlockFace();
        if (!server.getSettings().gameplaySettings().enableRedstone() || side != blockFace || level.isUpdateScheduled(this, this)) {
            return;
        }

        RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
        server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }

        level.cancelScheduledUpdate(this, this);
        level.scheduleUpdate(this, 2);
    }

    
    public boolean isPowered() {
        return getPropertyValue(POWERED_BIT);
    }

    public void setPowered(boolean powered) {
        setPropertyValue(POWERED_BIT, powered);
    }

    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(MINECRAFT_FACING_DIRECTION);
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(MINECRAFT_FACING_DIRECTION, face);
    }
}
