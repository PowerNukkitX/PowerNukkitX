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
    public static final BlockProperties $1 = new BlockProperties(FENCE_GATE, DIRECTION, IN_WALL_BIT, OPEN_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    // Contains a list of positions of fence gates, which have been opened by hand (by a player).
    // It is used to detect on redstone update, if the gate should be closed if redstone is off on the update,
    // previously the gate always closed, when placing an unpowered redstone at the gate, this fixes it
    // and gives the vanilla behavior; no idea how to make this better :d
    private static final Set<Location> manualOverrides = Sets.newConcurrentHashSet();
    /**
     * @deprecated 
     */
    

    public BlockFenceGate() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockFenceGate(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Oak Fence Gate";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 15;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
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

    
    /**
     * @deprecated 
     */
    private int getOffsetIndex() {
        return switch (getBlockFace()) {
            case SOUTH, NORTH -> 0;
            default -> 1;
        };
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinX() {
        return this.x + offMinX[getOffsetIndex()];
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinZ() {
        return this.z + offMinZ[getOffsetIndex()];
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxX() {
        return this.x + offMaxX[getOffsetIndex()];
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxZ() {
        return this.z + offMaxZ[getOffsetIndex()];
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        BlockFace $2 = player.getDirection();
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
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player != null) {
            Item $3 = player.getInventory().getItemInHand();
            if (player.isSneaking() && !(itemInHand.isTool() || itemInHand.isNull())) return false;
            return toggle(player);
        } else return false;
    }
    /**
     * @deprecated 
     */
    

    public boolean toggle(Player player) {
        if (!player.getAdventureSettings().get(AdventureSettings.Type.DOORS_AND_SWITCHED))
            return false;
        return this.setOpen(player, !this.isOpen());
    }
    /**
     * @deprecated 
     */
    

    public boolean setOpen(@Nullable Player player, boolean open) {
        if (open == this.isOpen()) {
            return false;
        }

        DoorToggleEvent $4 = new DoorToggleEvent(this, player);
        this.getLevel().getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        player = event.getPlayer();

        BlockFace direction;

        BlockFace $5 = getBlockFace();
        
        if (player != null) {
            double $6 = player.yaw;
            double $7 = (yaw - 90) % 360;

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

        var $8 = this.clone().add(0.5, 0.5, 0.5);
        VibrationEvent $9 = open ? new VibrationEvent(player != null ? player : this, source, VibrationType.BLOCK_OPEN) : new VibrationEvent(player != null ? player : this, source, VibrationType.BLOCK_CLOSE);
        this.level.getVibrationManager().callVibrationEvent(vibrationEvent);
        return true;
    }
    /**
     * @deprecated 
     */
    

    public void playOpenCloseSound() {
        if (this.isOpen()) {
            this.playOpenSound();
        } else {
            this.playCloseSound();
        }
    }
    /**
     * @deprecated 
     */
    

    public void playOpenSound() {
        level.addSound(this, Sound.RANDOM_DOOR_OPEN);
    }
    /**
     * @deprecated 
     */
    

    public void playCloseSound() {
        level.addSound(this, Sound.RANDOM_DOOR_CLOSE);
    }
    /**
     * @deprecated 
     */
    

    public boolean isOpen() {
        return getPropertyValue(OPEN_BIT);
    }
    /**
     * @deprecated 
     */
    

    public void setOpen(boolean open) {
        setPropertyValue(OPEN_BIT,open);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            BlockFace $10 = getBlockFace();
            boolean $11 = getSide(face.rotateY()) instanceof BlockWallBase || getSide(face.rotateYCCW()) instanceof BlockWallBase;
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

    
    /**
     * @deprecated 
     */
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
    /**
     * @deprecated 
     */
    

    public void setManualOverride(boolean val) {
        if (val) {
            manualOverrides.add(this.getLocation());
        } else {
            manualOverrides.remove(this.getLocation());
        }
    }
    /**
     * @deprecated 
     */
    

    public boolean getManualOverride() {
        return manualOverrides.contains(this.getLocation());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onBreak(Item item) {
        this.setManualOverride(false);
        return super.onBreak(item);
    }
    /**
     * @deprecated 
     */
    

    public boolean isInWall() {
        return getPropertyValue(IN_WALL_BIT);
    }
    /**
     * @deprecated 
     */
    

    public void setInWall(boolean inWall) {
        setPropertyValue(IN_WALL_BIT, inWall);
    }
    
    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getPropertyValue(DIRECTION));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setBlockFace(BlockFace face) {
        setPropertyValue(DIRECTION,face.getHorizontalIndex());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnChance() {
        return 5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnAbility() {
        return 20;
    }
}
