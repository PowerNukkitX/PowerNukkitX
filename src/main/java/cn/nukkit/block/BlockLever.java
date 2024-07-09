package cn.nukkit.block;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.block.property.enums.LeverDirection;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static cn.nukkit.block.property.CommonBlockProperties.LEVER_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.OPEN_BIT;

/**
 * @author Nukkit Project Team
 */

public class BlockLever extends BlockFlowable implements RedstoneComponent, Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(LEVER, LEVER_DIRECTION, OPEN_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLever() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLever(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Lever";
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 0.5d;
    }

    @Override
    public double getResistance() {
        return 2.5d;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    public boolean isPowerOn() {
        return getPropertyValue(OPEN_BIT);
    }

    public void setPowerOn(boolean powerOn) {
        setPropertyValue(OPEN_BIT, powerOn);
    }

    public LeverDirection getLeverOrientation() {
        return getPropertyValue(LEVER_DIRECTION);
    }

    public void setLeverOrientation(@Nullable LeverDirection value) {
        setPropertyValue(LEVER_DIRECTION, value);
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player!=null && !player.getAdventureSettings().get(AdventureSettings.Type.DOORS_AND_SWITCHED)) return false;
        if(isNotActivate(player)) return false;
        this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, isPowerOn() ? 15 : 0, isPowerOn() ? 0 : 15));
        setPowerOn(!isPowerOn());
        var pos = this.add(0.5, 0.5, 0.5);
        if (isPowerOn()) {
            this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(player != null ? player : this, pos, VibrationType.BLOCK_ACTIVATE));
        } else {
            this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(player != null ? player : this, pos, VibrationType.BLOCK_DEACTIVATE));
        }

        this.getLevel().setBlock(this, this, false, true);
        this.getLevel().addSound(this, Sound.RANDOM_CLICK, 0.8f, isPowerOn() ? 0.58f : 0.5f);

        LeverDirection orientation = getLeverOrientation();
        BlockFace face = orientation.getFacing();

        if (this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            updateAroundRedstone();
            RedstoneComponent.updateAroundRedstone(getSide(face.getOpposite()), face);
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            BlockFace blockFace = getLeverOrientation().getFacing().getOpposite();
            Block side = this.getSide(blockFace);
            if (!isSupportValid(side, blockFace.getOpposite())) {
                this.level.useBreakOn(this);
            }
        }
        return 0;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (target.canBeReplaced()) {
            target = target.down();
            face = BlockFace.UP;
        }

        if (!isSupportValid(target, face)) {
            return false;
        }
        setLeverOrientation(LeverDirection.forFacings(face, player.getHorizontalFacing()));
        return this.getLevel().setBlock(block, this, true, true);
    }

    /**
     * Check if the given block and its block face is a valid support for a lever
     *
     * @param support The block that the lever is being placed against
     * @param face    The face that the torch will be touching the block
     * @return If the support and face combinations can hold the lever
     */
    public static boolean isSupportValid(Block support, BlockFace face) {
        switch (support.getId()) {
            case FARMLAND, GRASS_PATH -> {
                return true;
            }
            default -> {
            }
        }

        if (face == BlockFace.DOWN) {
            return support.isSolid(BlockFace.DOWN) && (support.isFullBlock() || !support.isTransparent());
        }

        if (support.isSolid(face)) {
            return true;
        }

        if (support instanceof BlockWallBase || support instanceof BlockFence) {
            return face == BlockFace.UP;
        }

        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);

        if (isPowerOn()) {
            BlockFace face = getLeverOrientation().getFacing();
            this.level.updateAround(this.getLocation().getSide(face.getOpposite()));

            if (level.getServer().getSettings().levelSettings().enableRedstone()) {
                updateAroundRedstone();
                RedstoneComponent.updateAroundRedstone(getSide(face.getOpposite()), face);
            }
        }
        return true;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return isPowerOn() ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return !isPowerOn() ? 0 : getLeverOrientation().getFacing() == side ? 15 : 0;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return getLeverOrientation().getFacing();
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }
}
