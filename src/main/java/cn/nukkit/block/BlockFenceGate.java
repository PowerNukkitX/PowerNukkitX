package cn.nukkit.block;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.block.DoorToggleEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.RedstoneComponent;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Set;

import static cn.nukkit.block.property.CommonBlockProperties.DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.IN_WALL_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.OPEN_BIT;

/**
 * @author xtypr
 * @since 2015/11/23
 */
public class BlockFenceGate extends BlockTransparent implements RedstoneComponent, Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(FENCE_GATE, DIRECTION, IN_WALL_BIT, OPEN_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    // Contains a list of positions of fence gates, which have been opened by hand (by a player).
    // It is used to detect on redstone update, if the gate should be closed if redstone is off on the update,
    // previously the gate always closed, when placing an unpowered redstone at the gate, this fixes it
    // and gives the vanilla behavior; no idea how to make this better :d
    private static final Set<Location> manualOverrides = Sets.newConcurrentHashSet();

    public BlockFenceGate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockFenceGate(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Oak Fence Gate";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    private static final double[] offMinX = new double[2];
    private static final double[] offMinZ = new double[2];
    private static final double[] offMaxX = new double[2];
    private static final double[] offMaxZ = new double[2];

    static {
        offMinX[0] = 0;
        offMinZ[0] = 0.375;
        offMaxX[0] = 1;
        offMaxZ[0] = 0.625;

        offMinX[1] = 0.375;
        offMinZ[1] = 0;
        offMaxX[1] = 0.625;
        offMaxZ[1] = 1;
    }

    private int getOffsetIndex() {
        return switch (getBlockFace()) {
            case SOUTH, NORTH -> 0;
            default -> 1;
        };
    }

    @Override
    public double getMinX() {
        return this.x + offMinX[getOffsetIndex()];
    }

    @Override
    public double getMinZ() {
        return this.z + offMinZ[getOffsetIndex()];
    }

    @Override
    public double getMaxX() {
        return this.x + offMaxX[getOffsetIndex()];
    }

    @Override
    public double getMaxZ() {
        return this.z + offMaxZ[getOffsetIndex()];
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        BlockFace direction = player.getDirection();
        setBlockFace(direction);

        if (getSide(direction.rotateY()) instanceof BlockWallBase
                || getSide(direction.rotateYCCW()) instanceof BlockWallBase) {
            setInWall(true);
        }

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

    public boolean setOpen(@Nullable Player player, boolean open) {
        if (open == this.isOpen()) {
            return false;
        }

        DoorToggleEvent event = new DoorToggleEvent(this, player);
        this.getLevel().getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        player = event.getPlayer();

        BlockFace direction;

        BlockFace originDirection = getBlockFace();
        
        if (player != null) {
            double yaw = player.yaw;
            double rotation = (yaw - 90) % 360;

            if (rotation < 0) {
                rotation += 360.0;
            }

            if (originDirection.getAxis() == BlockFace.Axis.Z) {
                if (rotation >= 0 && rotation < 180) {
                    direction = BlockFace.NORTH;
                } else {
                    direction = BlockFace.SOUTH;
                }
            } else {
                if (rotation >= 90 && rotation < 270) {
                    direction = BlockFace.EAST;
                } else {
                    direction = BlockFace.WEST;
                }
            }
        } else {
            if (originDirection.getAxis() == BlockFace.Axis.Z) {
                direction = BlockFace.SOUTH;
            } else {
                direction = BlockFace.WEST;
            }
        }
        
        setBlockFace(direction);
        setPropertyValue(OPEN_BIT, !getPropertyValue(OPEN_BIT));
        this.level.setBlock(this, this, false, false);

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
        if (this.isOpen()) {
            this.playOpenSound();
        } else {
            this.playCloseSound();
        }
    }

    public void playOpenSound() {
        level.addSound(this, Sound.RANDOM_DOOR_OPEN);
    }

    public void playCloseSound() {
        level.addSound(this, Sound.RANDOM_DOOR_CLOSE);
    }

    public boolean isOpen() {
        return getPropertyValue(OPEN_BIT);
    }

    public void setOpen(boolean open) {
        setPropertyValue(OPEN_BIT,open);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            BlockFace face = getBlockFace();
            boolean touchingWall = getSide(face.rotateY()) instanceof BlockWallBase || getSide(face.rotateYCCW()) instanceof BlockWallBase;
            if (touchingWall != isInWall()) {
                this.setInWall(touchingWall);
                level.setBlock(this, this, true);
                return type;
            }
        } else if (type == Level.BLOCK_UPDATE_REDSTONE && this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            this.onRedstoneUpdate();
            return type;
        }

        return 0;
    }

    private void onRedstoneUpdate() {
        if ((this.isOpen() != this.isGettingPower()) && !this.getManualOverride()) {
            if (this.isOpen() != this.isGettingPower()) {
                level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, this.isOpen() ? 15 : 0, this.isOpen() ? 0 : 15));

                this.setOpen(null, this.isGettingPower());
            }
        } else if (this.getManualOverride() && (this.isGettingPower() == this.isOpen())) {
            this.setManualOverride(false);
        }
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

    public boolean isInWall() {
        return getPropertyValue(IN_WALL_BIT);
    }

    public void setInWall(boolean inWall) {
        setPropertyValue(IN_WALL_BIT, inWall);
    }
    
    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getPropertyValue(DIRECTION));
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(DIRECTION,face.getHorizontalIndex());
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }
}
