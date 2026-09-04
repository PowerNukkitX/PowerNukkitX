package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.event.redstone.RedstoneUpdateEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.TORCH_FACING_DIRECTION;

/**
 * @author Angelic47 (Nukkit Project)
 */

public class BlockRedstoneTorch extends BlockTorch implements RedstoneComponent {
    public static final BlockProperties PROPERTIES = new BlockProperties(REDSTONE_TORCH, TORCH_FACING_DIRECTION);
    public static final BlockDefinition DEFINITION = BlockTorch.DEFINITION.toBuilder()
            .lightEmission(7)
            .tickRate(2)
            .isPowerSource(true)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedstoneTorch() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedstoneTorch(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Redstone Torch";
    }

    
    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }

        if (this.level.getServer().getSettings().gameplaySettings().enableRedstone()) {
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

        if (this.level.getServer().getSettings().gameplaySettings().enableRedstone()) {
            updateAllAroundRedstone(getBlockFace().getOpposite());
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (super.onUpdate(type) == 0) {
            if (!this.level.getServer().getSettings().gameplaySettings().enableRedstone()) {
                return 0;
            }

            if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
                this.level.scheduleUpdate(this, tickRate());
            } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
                RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
                getLevel().getServer().getPluginManager().callEvent(ev);

                if (ev.isCancelled()) {
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
            this.level.setBlock(getLocation(), Block.get(BlockID.UNLIT_REDSTONE_TORCH).setPropertyValues(getPropertyValues()), false, true);
            updateAllAroundRedstone(getBlockFace().getOpposite());
            return true;
        }
        return false;
    }

    /**
     * Whether there is a power source in the opposite face of the current face
     */
    protected boolean isPoweredFromSide() {
        BlockFace face = getBlockFace().getOpposite();
        Block side = this.getSide(face);
        if (side instanceof BlockPistonBase && side.isGettingPower()) {
            return true;
        }

        return this.level.isSidePowered(side, face);
    }

}
