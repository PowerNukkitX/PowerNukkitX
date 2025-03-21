package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.CommonPropertyMap;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCalibratedSculkSensor;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static cn.nukkit.block.property.CommonBlockProperties.SCULK_SENSOR_PHASE;

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
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        setBlockFace(player != null ? BlockFace.fromHorizontalIndex(player.getDirection().getHorizontalIndex()) : BlockFace.SOUTH);

        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    public BlockFace getBlockFace() {
        return CommonPropertyMap.CARDINAL_BLOCKFACE.get(getPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION));
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

    @Override
    public int onUpdate(int type) {
        getOrCreateBlockEntity();
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (level.getServer().getSettings().gameplaySettings().enableRedstone()) {
                this.getBlockEntity().calPower();
                this.setPhase(0);
                updateAroundRedstone();
            }
            return type;
        }
        return 0;
    }

    public void setPhase(int phase) {
        if (phase == 1) this.level.addSound(this.add(0.5, 0.5, 0.5), Sound.POWER_ON_SCULK_SENSOR);
        else this.level.addSound(this.add(0.5, 0.5, 0.5), Sound.POWER_OFF_SCULK_SENSOR);
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