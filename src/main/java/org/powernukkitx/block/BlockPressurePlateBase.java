package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.PlayerHandle;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.Event;
import org.powernukkitx.event.block.BlockRedstoneEvent;
import org.powernukkitx.event.entity.EntityInteractEvent;
import org.powernukkitx.event.player.PlayerInteractEvent;
import org.powernukkitx.event.player.PlayerInteractEvent.Action;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.utils.RedstoneComponent;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.REDSTONE_SIGNAL;

/**
 * @author Snake1999
 * @since 2016/1/11
 */

public abstract class BlockPressurePlateBase extends BlockFlowable implements RedstoneComponent {
    public static final BlockDefinition DEFINITION = FLOWABLE.toBuilder()
            .canPassThrough(true)
            .isPowerSource(true)
            .waterloggingLevel(1)
            .build();
    protected float onPitch;
    protected float offPitch;

    public BlockPressurePlateBase(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockPressurePlateBase(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }

    protected abstract int computeRedstoneStrength();

    @Override
    public double getMinX() {
        return this.x + 0.625;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.625;
    }

    @Override
    public double getMinY() {
        return this.y + 0;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.9375;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.9375;
    }

    @Override
    public double getMaxY() {
        return isActivated() ? this.y + 0.03125 : this.y + 0.0625;
    }

    
    public boolean isActivated() {
        return getRedstonePower() == 0;
    }

    
    public static boolean isSupportValid(Block block, BlockFace blockFace) {
        return BlockLever.isSupportValid(block, blockFace) || block instanceof BlockFence;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isSupportValid(down(), BlockFace.UP)) {
                this.level.useBreakOn(this, ItemTool.getBestTool(getToolType()));
            }
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            int power = this.getRedstonePower();

            if (power > 0) {
                this.updateState(power);
            }
        }

        return 0;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (!isSupportValid(down(), BlockFace.UP)) {
            return false;
        }

        this.level.setBlock(block, this, true, true);
        return true;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return new SimpleAxisAlignedBB(this.x + 0.125, this.y, this.z + 0.125, this.x + 0.875, this.y + 0.25, this.z + 0.875D);
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (!this.level.getServer().getSettings().gameplaySettings().enableRedstone()) {
            return;
        }

        int power = getRedstonePower();

        if (power == 0) {
            Event ev;
            if (entity instanceof Player) {
                ev = new PlayerInteractEvent((Player) entity, null, this, null, Action.PHYSICAL);
            } else {
                ev = new EntityInteractEvent(entity, this);
            }

            this.level.getServer().getPluginManager().callEvent(ev);
            if(entity instanceof Player player) new PlayerHandle(player).setInteract();
            if (!ev.isCancelled()) {
                updateState(power);
            }
        }
    }

    protected void updateState(int oldStrength) {
        int strength = this.computeRedstoneStrength();
        boolean wasPowered = oldStrength > 0;
        boolean isPowered = strength > 0;

        if (oldStrength != strength) {
            this.setRedstonePower(strength);
            this.level.setBlock(this, this, false, false);

            updateAroundRedstone();
            RedstoneComponent.updateAroundRedstone(this.getSide(BlockFace.DOWN));

            if (!isPowered && wasPowered) {
                this.playOffSound();
                this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 15, 0));
            } else if (isPowered && !wasPowered) {
                this.playOnSound();
                this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 0, 15));
            }
        }

        if (isPowered) {
            this.level.scheduleUpdate(this, 20);
        }
    }

    @Override
    public boolean onBreak(Item item) {
        this.level.setBlock(this, Block.get(BlockID.AIR), true, true);

        if (this.getRedstonePower() > 0) {
            updateAroundRedstone();
            RedstoneComponent.updateAroundRedstone(this.getSide(BlockFace.DOWN));
        }

        return true;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return getRedstonePower();
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return side == BlockFace.UP ? this.getRedstonePower() : 0;
    }

    public int getRedstonePower() {
        return getPropertyValue(REDSTONE_SIGNAL);
    }

    public void setRedstonePower(int power) {
        setPropertyValue(REDSTONE_SIGNAL, power);
    }

    protected void playOnSound() {
        this.level.addLevelSoundEvent(this.add(0.5, 0.1, 0.5), SoundEvent.POWER_ON, getBlockState().blockStateHash());
    }

    protected void playOffSound() {
        this.level.addLevelSoundEvent(this.add(0.5, 0.1, 0.5), SoundEvent.POWER_OFF, getBlockState().blockStateHash());
    }
}
