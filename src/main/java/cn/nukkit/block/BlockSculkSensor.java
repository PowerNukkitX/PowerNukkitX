package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySculkSensor;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.SCULK_SENSOR_PHASE;

/**
 * @author LT_Name
 */
public class BlockSculkSensor extends BlockFlowable implements BlockEntityHolder<BlockEntitySculkSensor>, RedstoneComponent {
    public static final BlockProperties PROPERTIES = new BlockProperties(SCULK_SENSOR, SCULK_SENSOR_PHASE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSculkSensor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSculkSensor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Sculk Sensor";
    }

    @Override
    @NotNull public Class<? extends BlockEntitySculkSensor> getBlockEntityClass() {
        return BlockEntitySculkSensor.class;
    }

    @Override
    @NotNull public String getBlockEntityType() {
        return BlockEntity.SCULK_SENSOR;
    }

    @Override
    public boolean isPowerSource() {
        return true;
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
            if (level.getServer().getSettings().levelSettings().enableRedstone()) {
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
    public boolean isSolid() {
        return false;
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
