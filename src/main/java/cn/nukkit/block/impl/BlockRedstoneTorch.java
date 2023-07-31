package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockPistonBase;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Angelic47 (Nukkit Project)
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent and uses methods from it.", since = "1.4.0.0-PN")
public class BlockRedstoneTorch extends BlockTorch implements RedstoneComponent {

    public BlockRedstoneTorch() {
        this(0);
    }

    public BlockRedstoneTorch(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Redstone Torch";
    }

    @Override
    public int getId() {
        return REDSTONE_TORCH;
    }

    @Override
    public int getLightLevel() {
        return 7;
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
            Player player) {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }

        if (this.getLevel().getServer().isRedstoneEnabled()) {
            if (!checkState()) {
                updateAllAroundRedstone(getBlockFace().getOpposite());
            }

            checkState();
        }

        return true;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return getBlockFace() != side ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return side == BlockFace.DOWN ? this.getWeakPower(side) : 0;
    }

    @Override
    public boolean onBreak(Item item) {
        if (!super.onBreak(item)) {
            return false;
        }

        if (this.getLevel().getServer().isRedstoneEnabled()) {
            updateAllAroundRedstone(getBlockFace().getOpposite());
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (super.onUpdate(type) == 0) {
            if (!this.getLevel().getServer().isRedstoneEnabled()) {
                return 0;
            }

            if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
                this.getLevel().scheduleUpdate(this, tickRate());
            } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
                RedstoneUpdateEvent event = new RedstoneUpdateEvent(this);
                event.call();

                if (event.isCancelled()) {
                    return 0;
                }

                if (checkState()) {
                    return 1;
                }
            }
        }

        return 0;
    }

    private boolean checkState() {
        if (isPoweredFromSide()) {
            this.getLevel().setBlock(getLocation(), Block.get(BlockID.UNLIT_REDSTONE_TORCH, getDamage()), false, true);

            updateAllAroundRedstone(getBlockFace().getOpposite());
            return true;
        }

        return false;
    }

    @PowerNukkitDifference(
            info = "Check if the side block is piston and if piston is getting power.",
            since = "1.4.0.0-PN")
    protected boolean isPoweredFromSide() {
        BlockFace face = getBlockFace().getOpposite();
        if (this.getSide(face) instanceof BlockPistonBase && this.getSide(face).isGettingPower()) {
            return true;
        }

        return this.getLevel().isSidePowered(this.getLocation().getSide(face), face);
    }

    @Override
    public int tickRate() {
        return 2;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }
}
