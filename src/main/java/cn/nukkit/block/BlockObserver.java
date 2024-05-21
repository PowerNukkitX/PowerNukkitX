package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static cn.nukkit.block.property.CommonBlockProperties.MINECRAFT_FACING_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.POWERED_BIT;

/**
 * @author Leonidius20, joserobjr
 * @since 18.08.18
 */
public class BlockObserver extends BlockSolid implements RedstoneComponent, Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(OBSERVER, CommonBlockProperties.MINECRAFT_FACING_DIRECTION, POWERED_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockObserver() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockObserver(BlockState blockstate) {
        super(blockstate);
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
    public boolean isPowerSource() {
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
        if (!server.getSettings().levelSettings().enableRedstone() || side != blockFace || level.isUpdateScheduled(this, this)) {
            return;
        }

        RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
        server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }

        level.scheduleUpdate(this, 1);
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
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public double getHardness() {
        return 3.5;
    }

    @Override
    public double getResistance() {
        return 17.5;
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
