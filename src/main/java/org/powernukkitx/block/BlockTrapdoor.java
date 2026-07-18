package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.AdventureSettings;
import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.CommonPropertyMap;
import org.powernukkitx.event.block.BlockRedstoneEvent;
import org.powernukkitx.event.block.DoorToggleEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Location;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.vibration.VibrationEvent;
import org.powernukkitx.level.vibration.VibrationType;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.BlockFace.AxisDirection;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.utils.Faceable;
import org.powernukkitx.utils.RedstoneComponent;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Set;

public class BlockTrapdoor extends BlockTransparent implements RedstoneComponent, Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(TRAPDOOR,
            CommonBlockProperties.DIRECTION,
            CommonBlockProperties.OPEN_BIT,
            CommonBlockProperties.UPSIDE_DOWN_BIT);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(3)
            .resistance(15)
            .toolType(ItemTool.TYPE_AXE)
            .canBeActivated(true)
            .build();

    private static final double THICKNESS = 0.1875;

    // Contains a list of positions of trap doors, which have been opened by hand (by a player).
    // It is used to detect on redstone update, if the door should be closed if redstone is off on the update,
    // previously the door always closed, when placing an unpowered redstone at the door, this fixes it
    // and gives the vanilla behavior; no idea how to make this better :d
    private static final Set<Location> manualOverrides = Sets.newConcurrentHashSet();

    public BlockTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTrapdoor(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Oak Trapdoor";
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public double getMinX() {
        return this.x + getRelativeBoundingBox().getMinX();
    }

    @Override
    public double getMaxX() {
        return this.x + getRelativeBoundingBox().getMaxX();
    }

    @Override
    public double getMinY() {
        return this.y + getRelativeBoundingBox().getMinY();
    }

    @Override
    public double getMaxY() {
        return this.y + getRelativeBoundingBox().getMaxY();
    }

    @Override
    public double getMinZ() {
        return this.z + getRelativeBoundingBox().getMinZ();
    }

    @Override
    public double getMaxZ() {
        return this.z + getRelativeBoundingBox().getMaxZ();
    }

    private AxisAlignedBB getRelativeBoundingBox() {
        int value = this.blockstate.specialValue();
        BlockProperties props = getProperties();

        boolean open = props.getPropertyValue(value, CommonBlockProperties.OPEN_BIT);
        boolean top = props.getPropertyValue(value, CommonBlockProperties.UPSIDE_DOWN_BIT);

        AxisAlignedBB aabb;

        if (open) {
            BlockFace face = CommonPropertyMap.EWSN_DIRECTION.inverse().get(
                    props.getPropertyValue(value, CommonBlockProperties.DIRECTION)).getOpposite();

            if (face.getAxisDirection() == AxisDirection.NEGATIVE) {
                aabb = new SimpleAxisAlignedBB(
                        0,
                        0,
                        0,
                        1 + face.getXOffset() - (THICKNESS * face.getXOffset()),
                        1,
                        1 + face.getZOffset() - (THICKNESS * face.getZOffset())
                );
            } else {
                aabb = new SimpleAxisAlignedBB(
                        face.getXOffset() - (THICKNESS * face.getXOffset()),
                        0,
                        face.getZOffset() - (THICKNESS * face.getZOffset()),
                        1,
                        1,
                        1
                );
            }
        } else if (top) {
            aabb = new SimpleAxisAlignedBB(0, 1 - THICKNESS, 0, 1, 1, 1);
        } else {
            aabb = new SimpleAxisAlignedBB(0, 0, 0, 1, THICKNESS, 1);
        }

        return aabb;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_REDSTONE && this.level.getServer().getSettings().gameplaySettings().enableRedstone()) {
            if ((this.isOpen() != this.isGettingPower()) && !this.getManualOverride()) {
                level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this,
                        this.isOpen() ? 15 : 0, this.isOpen() ? 0 : 15));
                this.setOpen(null, this.isGettingPower());
            } else if (this.getManualOverride() && (this.isGettingPower() == this.isOpen())) {
                this.setManualOverride(false);
            }
            return type;
        }
        return 0;
    }

    public void setManualOverride(boolean val) {
        if (val) {
            manualOverrides.add(this.getLocation());
        } else {
            manualOverrides.remove(this.getLocation());
        }
    }

    public boolean getManualOverride() {
        return manualOverrides.contains(this.getLocation());
    }

    @Override
    public boolean onBreak(Item item) {
        this.setManualOverride(false);
        return super.onBreak(item);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target,
                         @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        setBlockFace(player == null ? face : player.getDirection().getOpposite());
        setTop(face.getAxis().isHorizontal() ? fy > 0.5 : face != BlockFace.UP);

        if (!this.getLevel().setBlock(block, this, true, true)) {
            return false;
        }

        if (level.getServer().getSettings().gameplaySettings().enableRedstone() &&
                !this.isOpen() && this.isGettingPower()) {
            this.setOpen(null, true);
        }

        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player != null) {
            Item itemInHand = player.getInventory().getItemInMainHand();
            if (player.isSneaking() && !(itemInHand.isTool() || itemInHand.isNull())) return false;
        }
        return toggle(player);
    }

    public boolean toggle(Player player) {
        if (player != null && !player.getAdventureSettings().get(AdventureSettings.Type.DOORS_AND_SWITCHED)) {
            return false;
        }
        return this.setOpen(player, !this.isOpen());
    }

    public boolean setOpen(Player player, boolean open) {
        if (open == this.isOpen()) {
            return false;
        }

        DoorToggleEvent event = new DoorToggleEvent(this, player);
        level.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        player = event.getPlayer();
        setPropertyValue(CommonBlockProperties.OPEN_BIT, open);

        if (!level.setBlock(this, this, true, true)) return false;

        if (player != null) {
            this.setManualOverride(this.isGettingPower() || isOpen());
        }

        playOpenCloseSound();

        var source = this.getVector3().add(0.5, 0.5, 0.5);
        VibrationEvent vibrationEvent = open
                ? new VibrationEvent(player != null ? player : this, source, VibrationType.BLOCK_OPEN)
                : new VibrationEvent(player != null ? player : this, source, VibrationType.BLOCK_CLOSE);

        this.level.getVibrationManager().callVibrationEvent(vibrationEvent);
        return true;
    }

    public void playOpenCloseSound() {
        if (isOpen()) {
            playOpenSound();
        } else {
            playCloseSound();
        }
    }

    public void playOpenSound() {
        this.level.addSound(this, Sound.RANDOM_DOOR_OPEN);
    }

    public void playCloseSound() {
        this.level.addSound(this, Sound.RANDOM_DOOR_CLOSE);
    }

    public boolean isOpen() {
        return getPropertyValue(CommonBlockProperties.OPEN_BIT);
    }

    public void setOpen(boolean open) {
        setPropertyValue(CommonBlockProperties.OPEN_BIT, open);
    }

    public boolean isTop() {
        return getPropertyValue(CommonBlockProperties.UPSIDE_DOWN_BIT);
    }

    public void setTop(boolean top) {
        setPropertyValue(CommonBlockProperties.UPSIDE_DOWN_BIT, top);
    }

    @Override
    public BlockFace getBlockFace() {
        return CommonPropertyMap.EWSN_DIRECTION.inverse().get(getPropertyValue(CommonBlockProperties.DIRECTION));
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.DIRECTION, CommonPropertyMap.EWSN_DIRECTION.get(face));
    }
}
