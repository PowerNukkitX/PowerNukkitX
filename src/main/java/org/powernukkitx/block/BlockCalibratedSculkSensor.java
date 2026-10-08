package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.CommonPropertyMap;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityCalibratedSculkSensor;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.vibration.VibrationListenerStorage;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static org.powernukkitx.block.property.CommonBlockProperties.SCULK_SENSOR_PHASE;

public class BlockCalibratedSculkSensor extends BlockFlowable implements BlockEntityHolder<BlockEntityCalibratedSculkSensor>, RedstoneComponent {
    public static final BlockProperties PROPERTIES = new BlockProperties(CALIBRATED_SCULK_SENSOR, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.SCULK_SENSOR_PHASE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCalibratedSculkSensor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCalibratedSculkSensor(BlockState blockstate) {
        super(blockstate);
    }

    public String getName() {
        return "Calibrated Sculk Sensor";
    }

    public void setBlockFace(BlockFace face) {
        int horizontalIndex = face.getHorizontalIndex();
        if (horizontalIndex > -1) {
            this.setPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION,
                    CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(BlockFace.fromHorizontalIndex(horizontalIndex)));
        }
    }

    @Override
    @NotNull public Class<? extends BlockEntityCalibratedSculkSensor> getBlockEntityClass() {
        return BlockEntityCalibratedSculkSensor.class;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    @NotNull public String getBlockEntityType() {
        return BlockEntity.CALIBRATED_SCULK_SENSOR;
    }

    @Override
    @NotNull public BlockEntityCalibratedSculkSensor createBlockEntity() {
        return createBlockEntity(VibrationListenerStorage.createInitialData());
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        setBlockFace(player != null ? player.getDirection().getOpposite() : BlockFace.SOUTH);
        return BlockEntityHolder.setBlockAndCreateEntity(this, false, true, VibrationListenerStorage.createInitialData()) != null;
    }

    public BlockFace getBlockFace() {
        return CommonPropertyMap.CARDINAL_BLOCKFACE.get(getPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION));
    }

    /**
     * Returns the redstone signal entering the calibration input side.
     *
     * @return calibration input strength
     */
    public int getInputStrength() {
        BlockFace inputFace = getBlockFace();
        return level.getRedstonePower(getSide(inputFace), inputFace);
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return super.getStrongPower(side);
    }

    @Override
    public int getWeakPower(BlockFace face) {
        var blockEntity = this.getOrCreateBlockEntity();
        if (this.getSide(face.getOpposite()) instanceof BlockRedstoneComparator) {
            return blockEntity.getComparatorPower();
        } else {
            return blockEntity.getPower();
        }
    }

    /**
     * Returns whether the sensor is in its inactive phase.
     *
     * @return whether the sensor is inactive
     */
    public boolean isInactive() {
        return getPropertyValue(SCULK_SENSOR_PHASE) == 0;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (level.getServer().getSettings().gameplaySettings().enableRedstone()) {
                if (getPropertyValue(SCULK_SENSOR_PHASE) == 1) {
                    this.getBlockEntity().clearPower();
                    this.setPhase(2);
                    updateAroundRedstone();
                    level.scheduleUpdate(this, 10);
                } else if (getPropertyValue(SCULK_SENSOR_PHASE) == 2) {
                    this.setPhase(0);
                }
            }
            return type;
        }
        return 0;
    }

    public void setPhase(int phase) {
        int oldPhase = getPropertyValue(SCULK_SENSOR_PHASE);
        if (phase == 1) this.level.addSound(this.add(0.5, 0.5, 0.5), Sound.POWER_ON_SCULK_SENSOR);
        else if (phase == 0 && oldPhase != 0) this.level.addSound(this.add(0.5, 0.5, 0.5), Sound.POWER_OFF_SCULK_SENSOR);
        this.setPropertyValue(SCULK_SENSOR_PHASE, phase);
        this.level.setBlock(this, this, true, false);
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public boolean breaksWhenMoved() {
        return false;
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }
}