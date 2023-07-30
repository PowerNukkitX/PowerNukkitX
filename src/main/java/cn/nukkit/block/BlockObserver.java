package cn.nukkit.block;

import static cn.nukkit.blockproperty.CommonBlockProperties.POWERED;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.RedstoneComponent;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

/**
 * @author Leonidius20, joserobjr
 * @since 18.08.18
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent and uses methods from it.", since = "1.4.0.0-PN")
public class BlockObserver extends BlockSolidMeta implements RedstoneComponent, Faceable {
    @Since("1.20.10-r1")
    @PowerNukkitXOnly
    public static final BlockProperty<BlockFace> FACING_DIRECTION =
            new ArrayBlockProperty<>("minecraft:facing_direction", false, new BlockFace[] {
                // Index based
                BlockFace.DOWN, BlockFace.UP,
                BlockFace.NORTH, BlockFace.SOUTH,
                BlockFace.WEST, BlockFace.EAST,
            });

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(FACING_DIRECTION, POWERED);

    public BlockObserver() {
        this(0);
    }

    public BlockObserver(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Observer";
    }

    @Override
    public int getId() {
        return OBSERVER;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public boolean place(
            @NotNull Item item,
            @NotNull Block block,
            @NotNull Block target,
            @NotNull BlockFace face,
            double fx,
            double fy,
            double fz,
            @Nullable Player player) {
        if (player != null) {
            if (Math.abs(player.getFloorX() - this.x()) <= 1 && Math.abs(player.getFloorZ() - this.z()) <= 1) {
                double y = player.y() + player.getEyeHeight();
                if (y - this.y() > 2) {
                    setBlockFace(BlockFace.DOWN);
                } else if (this.y() - y > 0) {
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

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implemented")
    @Override
    public boolean isPowerSource() {
        return true;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implemented")
    @Override
    public int getStrongPower(BlockFace side) {
        return isPowered() && side == getBlockFace() ? 15 : 0;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implemented")
    @Override
    public int getWeakPower(BlockFace face) {
        return getStrongPower(face);
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implemented")
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED || type == Level.BLOCK_UPDATE_MOVED) {
            RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
            PluginManager pluginManager = getLevel().getServer().getPluginManager();
            pluginManager.callEvent(ev);
            if (ev.isCancelled()) {
                return 0;
            }

            if (!isPowered()) {
                getLevel().getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 0, 15));
                setPowered(true);

                if (getLevel().setBlock(this, this)) {
                    getSide(getBlockFace().getOpposite()).onUpdate(Level.BLOCK_UPDATE_REDSTONE);
                    RedstoneComponent.updateAroundRedstone(
                            getSide(getBlockFace().getOpposite()));
                    getLevel().scheduleUpdate(this, 2);
                }
            } else {
                pluginManager.callEvent(new BlockRedstoneEvent(this, 15, 0));
                setPowered(false);

                getLevel().setBlock(this, this);
                getSide(getBlockFace().getOpposite()).onUpdate(Level.BLOCK_UPDATE_REDSTONE);
                RedstoneComponent.updateAroundRedstone(getSide(getBlockFace().getOpposite()));
            }
            return type;
        }
        return 0;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void onNeighborChange(@NotNull BlockFace side) {
        Server server = getLevel().getServer();
        BlockFace blockFace = getBlockFace();
        if (!server.isRedstoneEnabled() || side != blockFace || getLevel().isUpdateScheduled(this, this)) {
            return;
        }

        RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
        server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }

        getLevel().scheduleUpdate(this, 1);
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
    @PowerNukkitOnly
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

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isPowered() {
        return getBooleanValue(POWERED);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setPowered(boolean powered) {
        setBooleanValue(POWERED, powered);
    }

    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(FACING_DIRECTION);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(FACING_DIRECTION, face);
    }
}
