package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

//todo complete feature
public class BlockCalibratedSculkSensor extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(CALIBRATED_SCULK_SENSOR, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.SCULK_SENSOR_PHASE);

    @Override
    public @NotNull BlockProperties getProperties() {
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
}