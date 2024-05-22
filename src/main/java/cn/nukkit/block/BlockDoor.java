package cn.nukkit.block;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.block.DoorToggleEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
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
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockDoor extends BlockTransparent implements RedstoneComponent, Faceable {
    private static final double THICKNESS = 3.0 / 16;

    // Contains a list of positions of doors, which have been opened by hand (by a player).
    // It is used to detect on redstone update, if the door should be closed if redstone is off on the update,
    // previously the door always closed, when placing an unpowered redstone at the door, this fixes it
    // and gives the vanilla behavior; no idea how to make this better :d
    private static final List<Location> manualOverrides = new ArrayList<>();

    protected final static BiMap<BlockFace, Integer> DOOR_DIRECTION = HashBiMap.create(4);

    static {
        DOOR_DIRECTION.put(BlockFace.EAST, 0);
        DOOR_DIRECTION.put(BlockFace.SOUTH, 1);
        DOOR_DIRECTION.put(BlockFace.WEST, 2);
        DOOR_DIRECTION.put(BlockFace.NORTH, 3);
    }

    public BlockDoor(BlockState blockState) {
        super(blockState);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        BlockFace position = getBlockFace().getOpposite();
        boolean isOpen = isOpen();
        boolean isRight = isRightHinged();

        if (isOpen) {
            return recalculateBoundingBoxWithPos(isRight ? position.rotateYCCW() : position.rotateY());
        } else {
            return recalculateBoundingBoxWithPos(position);
        }
    }

    private AxisAlignedBB recalculateBoundingBoxWithPos(BlockFace pos) {
        if (pos.getAxisDirection() == AxisDirection.NEGATIVE) {
            return new SimpleAxisAlignedBB(
                    this.x,
                    this.y,
                    this.z,
                    this.x + 1 + pos.getXOffset() - (THICKNESS * pos.getXOffset()),
                    this.y + 1,
                    this.z + 1 + pos.getZOffset() - (THICKNESS * pos.getZOffset())
            );
        } else {
            return new SimpleAxisAlignedBB(
                    this.x + pos.getXOffset() - (THICKNESS * pos.getXOffset()),
                    this.y,
                    this.z + pos.getZOffset() - (THICKNESS * pos.getZOffset()),
                    this.x + 1,
                    this.y + 1,
                    this.z + 1
            );
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            this.onNormalUpdate();
            return type;
        }

        if (type == Level.BLOCK_UPDATE_REDSTONE && level.getServer().getSettings().levelSettings().enableRedstone()) {
            this.onRedstoneUpdate();
            return type;
        }

        return 0;
    }

    private void onNormalUpdate() {
        Block down = this.down();
        if (isTop()) {

            if (!down.getId().equals(this.getId()) || down.getPropertyValue(CommonBlockProperties.UPPER_BLOCK_BIT)) {
                level.setBlock(this, Block.get(AIR), false);
            }

            /* Doesn't work with redstone
            boolean downIsOpen = down.getBooleanValue(OPEN);
            if (downIsOpen != isOpen()) {
                setOpen(downIsOpen);
                level.setBlock(this, this, false, true);
            }*/
            return;
        }

        if (down.getId().equals(AIR)) {
            level.useBreakOn(this, getToolType() == ItemTool.TYPE_PICKAXE ? Item.get(ItemID.DIAMOND_PICKAXE) : null);
        }
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
        Location down;
        Location up;
        if (this.isTop()) {
            down = down().getLocation();
            up = getLocation();
        } else {
            down = getLocation();
            up = up().getLocation();
        }

        if (val) {
            manualOverrides.add(up);
            manualOverrides.add(down);
        } else {
            manualOverrides.remove(up);
            manualOverrides.remove(down);
        }
    }

    public boolean getManualOverride() {
        Location down;
        Location up;
        if (this.isTop()) {
            down = down().getLocation();
            up = getLocation();
        } else {
            down = getLocation();
            up = up().getLocation();
        }

        return manualOverrides.contains(up) || manualOverrides.contains(down);
    }

    @Override
    public boolean isGettingPower() {
        Location down;
        Location up;
        if (this.isTop()) {
            down = down().getLocation();
            up = getLocation();
        } else {
            down = getLocation();
            up = up().getLocation();
        }

        for (BlockFace side : BlockFace.values()) {
            Block blockDown = down.getSide(side).getLevelBlock();
            Block blockUp = up.getSide(side).getLevelBlock();

            if (this.level.isSidePowered(blockDown.getLocation(), side)
                    || this.level.isSidePowered(blockUp.getLocation(), side)) {
                return true;
            }
        }

        return this.level.isBlockPowered(down) || this.level.isBlockPowered(up);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (this.y > this.level.getMaxHeight() - 1 || face != BlockFace.UP) {
            return false;
        }

        Block blockUp = this.up();
        Block blockDown = this.down();
        if (!blockUp.canBeReplaced() || !blockDown.isSolid(BlockFace.UP) && !(blockDown instanceof BlockCauldron)) {
            return false;
        }

        BlockFace direction = player != null ? player.getDirection() : BlockFace.SOUTH;
        setBlockFace(direction);

        Block left = this.getSide(direction.rotateYCCW());
        Block right = this.getSide(direction.rotateY());
        if (left.getId().equals(this.getId()) || (!right.isTransparent() && left.isTransparent())) { //Door hinge
            setRightHinged(true);
        }

        setTop(false);

        level.setBlock(block, this, true, false); //Bottom

        if (blockUp instanceof BlockLiquid liquid && liquid.usesWaterLogging()) {
            level.setBlock(blockUp, 1, blockUp, true, false);
        }

        BlockDoor doorTop = (BlockDoor) clone();
        doorTop.y++;
        doorTop.setTop(true);
        level.setBlock(doorTop, doorTop, true, true); //Top

        level.updateAround(block);

        if (level.getServer().getSettings().levelSettings().enableRedstone() && !this.isOpen() && this.isGettingPower()) {
            this.setOpen(null, true);
        }

        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        this.setManualOverride(false);
        if (isTop()) {
            Block down = this.down();
            if (down.getId().equals(this.getId()) && !down.getPropertyValue(CommonBlockProperties.UPPER_BLOCK_BIT)) {
                level.setBlock(down, Block.get(AIR), true);
            }
        } else {
            Block up = this.up();
            if (up.getId().equals(this.getId()) && up.getPropertyValue(CommonBlockProperties.UPPER_BLOCK_BIT)) {
                level.setBlock(up, Block.get(BlockID.AIR), true);
            }
        }
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true);

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

    public void playOpenCloseSound() {
        if (this.isTop() && down() instanceof BlockDoor) {
            if (((BlockDoor) down()).isOpen()) {
                this.playOpenSound();
            } else {
                this.playCloseSound();
            }
        } else if (up() instanceof BlockDoor) {
            if (this.isOpen()) {
                this.playOpenSound();
            } else {
                this.playCloseSound();
            }
        }
    }

    public void playOpenSound() {
        level.addSound(this, Sound.RANDOM_DOOR_OPEN);
    }

    public void playCloseSound() {
        level.addSound(this, Sound.RANDOM_DOOR_CLOSE);
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
        level.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        player = event.getPlayer();

        Block down;
        Block up;
        if (this.isTop()) {
            down = down();
            up = this;
        } else {
            down = this;
            up = up();
        }

        up.setPropertyValue(CommonBlockProperties.OPEN_BIT, open);
        up.level.setBlock(up, up, true, true);

        down.setPropertyValue(CommonBlockProperties.OPEN_BIT, open);
        down.level.setBlock(down, down, true, true);

        if (player != null) {
            this.setManualOverride(this.isGettingPower() || isOpen());
        }

        playOpenCloseSound();

        var source = this.clone().add(0.5, 0.5, 0.5);
        VibrationEvent vibrationEvent = open ? new VibrationEvent(player != null ? player : this, source, VibrationType.BLOCK_OPEN) : new VibrationEvent(player != null ? player : this, source, VibrationType.BLOCK_CLOSE);
        this.level.getVibrationManager().callVibrationEvent(vibrationEvent);
        return true;
    }

    public void setOpen(boolean open) {
        setPropertyValue(CommonBlockProperties.OPEN_BIT, open);
    }

    public boolean isOpen() {
        return getPropertyValue(CommonBlockProperties.OPEN_BIT);
    }

    public boolean isTop() {
        return getPropertyValue(CommonBlockProperties.UPPER_BLOCK_BIT);
    }

    public void setTop(boolean top) {
        setPropertyValue(CommonBlockProperties.UPPER_BLOCK_BIT, top);
    }

    public boolean isRightHinged() {
        return getPropertyValue(CommonBlockProperties.DOOR_HINGE_BIT);
    }

    public void setRightHinged(boolean rightHinged) {
        setPropertyValue(CommonBlockProperties.DOOR_HINGE_BIT, rightHinged);
    }

    @Override
    public BlockFace getBlockFace() {
        return DOOR_DIRECTION.inverse().get(getPropertyValue(CommonBlockProperties.DIRECTION));
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.DIRECTION, DOOR_DIRECTION.get(face));
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }
}
