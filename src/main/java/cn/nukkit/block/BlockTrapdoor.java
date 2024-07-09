package cn.nukkit.block;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.CommonPropertyMap;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.block.DoorToggleEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.AxisDirection;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.RedstoneComponent;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author Pub4Game
 * @since 26.12.2015
 */
public class BlockTrapdoor extends BlockTransparent implements RedstoneComponent, Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(TRAPDOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPSIDE_DOWN_BIT);

    private static final double THICKNESS = 0.1875;

    // Contains a list of positions of trap doors, which have been opened by hand (by a player).
    // It is used to detect on redstone update, if the door should be closed if redstone is off on the update,
    // previously the door always closed, when placing an unpowered redstone at the door, this fixes it
    // and gives the vanilla behavior; no idea how to make this better :d
    private static final Set<Location> manualOverrides = Sets.newConcurrentHashSet();

    private static final AxisAlignedBB[] boundingBox2SpecialV = new AxisAlignedBB[0x1 << PROPERTIES.getSpecialValueBits()];

    //<editor-fold desc="pre-computing the bounding boxes" defaultstate="collapsed">
    static {
        for (int specialValue = 0; specialValue < boundingBox2SpecialV.length; specialValue++) {
            AxisAlignedBB bb;
            if (PROPERTIES.getPropertyValue(specialValue, CommonBlockProperties.OPEN_BIT)) {
                BlockFace face = CommonPropertyMap.EWSN_DIRECTION.inverse().get(PROPERTIES.getPropertyValue(specialValue, CommonBlockProperties.DIRECTION));
                face = face.getOpposite();
                if (face.getAxisDirection() == AxisDirection.NEGATIVE) {
                    bb = new SimpleAxisAlignedBB(
                            0,
                            0,
                            0,
                            1 + face.getXOffset() - (THICKNESS * face.getXOffset()),
                            1,
                            1 + face.getZOffset() - (THICKNESS * face.getZOffset())
                    );
                } else {
                    bb = new SimpleAxisAlignedBB(
                            face.getXOffset() - (THICKNESS * face.getXOffset()),
                            0,
                            face.getZOffset() - (THICKNESS * face.getZOffset()),
                            1,
                            1,
                            1
                    );
                }
            } else if (PROPERTIES.getPropertyValue(specialValue, CommonBlockProperties.UPSIDE_DOWN_BIT)) {
                bb = new SimpleAxisAlignedBB(
                        0,
                        1 - THICKNESS,
                        0,
                        1,
                        1,
                        1
                );
            } else {
                bb = new SimpleAxisAlignedBB(
                        0,
                        0,
                        0,
                        1,
                        0 + THICKNESS,
                        1
                );
            }
            boundingBox2SpecialV[specialValue] = bb;
        }
    }
    //</editor-fold>

    public BlockTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTrapdoor(BlockState blockState) {
        super(blockState);
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
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    private AxisAlignedBB getRelativeBoundingBox() {
        return boundingBox2SpecialV[this.blockstate.specialValue()];
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

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_REDSTONE && this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            if ((this.isOpen() != this.isGettingPower()) && !this.getManualOverride()) {
                if (this.isOpen() != this.isGettingPower()) {
                    level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, this.isOpen() ? 15 : 0, this.isOpen() ? 0 : 15));

                    this.setOpen(null, this.isGettingPower());
                }
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
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        setBlockFace(player == null ? face : player.getDirection().getOpposite());
        setTop(face.getAxis().isHorizontal() ? fy > 0.5 : face != BlockFace.UP);

        if (!this.getLevel().setBlock(block, this, true, true)) {
            return false;
        }

        if (level.getServer().getSettings().levelSettings().enableRedstone() && !this.isOpen() && this.isGettingPower()) {
            this.setOpen(null, true);
        }

        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player != null) {
            Item itemInHand = player.getInventory().getItemInHand();
            if (player.isSneaking() && !(itemInHand.isTool() || itemInHand.isNull())) return false;
            return toggle(player);
        } else return false;
    }

    public boolean toggle(Player player) {
        if (!player.getAdventureSettings().get(AdventureSettings.Type.DOORS_AND_SWITCHED))
            return false;
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
        if (!level.setBlock(this, this, true, true))
            return false;

        if (player != null) {
            this.setManualOverride(this.isGettingPower() || isOpen());
        }

        playOpenCloseSound();

        var source = this.clone().add(0.5, 0.5, 0.5);
        VibrationEvent vibrationEvent = open ? new VibrationEvent(player != null ? player : this, source, VibrationType.BLOCK_OPEN) : new VibrationEvent(player != null ? player : this, source, VibrationType.BLOCK_CLOSE);
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
