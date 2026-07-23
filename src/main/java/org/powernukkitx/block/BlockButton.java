package org.powernukkitx.block;

import org.powernukkitx.AdventureSettings;
import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.event.block.BlockRedstoneEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.vibration.VibrationEvent;
import org.powernukkitx.level.vibration.VibrationType;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.Faceable;
import org.powernukkitx.utils.RedstoneComponent;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.powernukkitx.block.definition.BlockDefinition;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author CreeperFace
 * @since 27. 11. 2016
 */

public abstract class BlockButton extends BlockFlowable implements RedstoneComponent, Faceable {
    public static final BlockDefinition DEFINITION = FLOWABLE.toBuilder()
            .resistance(2.5)
            .hardness(0.5)
            .canBeFlowedInto(false)
            .canBeActivated(true)
            .isPowerSource(true)
            .waterloggingLevel(1)
            .build();

    public BlockButton(BlockState meta) {
        super(meta);
    }

    public BlockButton(BlockState meta, BlockDefinition definition) {
        super(meta, definition);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (!BlockLever.isSupportValid(target, face)) {
            return false;
        }

        setBlockFace(face);
        this.level.setBlock(block, this, true, true);
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if(player != null) {
            if (!player.getAdventureSettings().get(AdventureSettings.Type.DOORS_AND_SWITCHED))
                return false;
            Item itemInHand = player.getInventory().getItemInMainHand();
            if (player.isSneaking() && !(itemInHand.isTool() || itemInHand.isNull())) return false;
        }
        if (this.isActivated()) {
            return false;
        }

        this.level.scheduleUpdate(this, 30);

        setActivated(true, player);
        this.level.setBlock(this, this, true, false);
        this.level.addLevelSoundEvent(this.add(0.5, 0.5, 0.5), SoundEvent.POWER_ON, this.getBlockState().blockStateHash());
        if (this.level.getServer().getSettings().gameplaySettings().enableRedstone()) {
            this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 0, 15));

            updateAroundRedstone();
            RedstoneComponent.updateAroundRedstone(getSide(getFacing().getOpposite()), getFacing());
        }

        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            BlockFace thisFace = getFacing();
            BlockFace touchingFace = thisFace.getOpposite();
            Block side = this.getSide(touchingFace);
            if (!BlockLever.isSupportValid(side, thisFace)) {
                this.level.useBreakOn(this, Item.get(Item.WOODEN_PICKAXE));
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (this.isActivated()) {
                setActivated(false);
                this.level.setBlock(this, this, true, false);
                this.level.addLevelSoundEvent(this.add(0.5, 0.5, 0.5), SoundEvent.POWER_OFF, this.getBlockState().blockStateHash());

                if (this.level.getServer().getSettings().gameplaySettings().enableRedstone()) {
                    this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 15, 0));

                    updateAroundRedstone();
                    RedstoneComponent.updateAroundRedstone(getSide(getFacing().getOpposite()), getFacing());
                }
            }
            return Level.BLOCK_UPDATE_SCHEDULED;
        }

        return 0;
    }

    public boolean isActivated() {
        return getPropertyValue(CommonBlockProperties.BUTTON_PRESSED_BIT);
    }

    public void setActivated(boolean activated) {
        setActivated(activated, null);
    }

    public void setActivated(boolean activated, @Nullable Player player) {
        setPropertyValue(CommonBlockProperties.BUTTON_PRESSED_BIT, activated);
        var pos = this.add(0.5, 0.5, 0.5);
        if (activated) {
            this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(player != null ? player : this, pos, VibrationType.BLOCK_ACTIVATE));
        } else {
            this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(player != null ? player : this, pos, VibrationType.BLOCK_DEACTIVATE));
        }
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return isActivated() ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return !isActivated() ? 0 : (getFacing() == side ? 15 : 0);
    }

    public BlockFace getFacing() {
        return BlockFace.fromIndex(getPropertyValue(CommonBlockProperties.FACING_DIRECTION));
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.FACING_DIRECTION, face.getIndex());
    }

    @Override
    public boolean onBreak(Item item) {
        if (isActivated()) {
            if (level.getServer().getSettings().gameplaySettings().enableRedstone()) {
                BlockFace face = this.getFacing();
                this.level.updateAround(this.getLocation().getSide(face.getOpposite()));
                this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 15, 0));
            }
        }
        return super.onBreak(item);
    }

    @Override
    public BlockFace getBlockFace() {
        return getFacing();
    }
}
