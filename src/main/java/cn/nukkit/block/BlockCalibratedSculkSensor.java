package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.math.AxisAlignedBB;
import org.jetbrains.annotations.NotNull;

//todo complete feature
public class BlockCalibratedSculkSensor extends BlockFlowable {
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