package cn.nukkit.block;

import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import org.jetbrains.annotations.NotNull;

//todo complete


public class BlockCalibratedSculkSensor extends BlockTransparentMeta {
    public static final IntBlockProperty SCULK_SENSOR_PHASE = new IntBlockProperty("sculk_sensor_phase", false, 2);

    public static final BlockProperties PROPERTIES = new BlockProperties(CommonBlockProperties.CARDINAL_DIRECTION, SCULK_SENSOR_PHASE);

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCalibratedSculkSensor() {
    }

    public BlockCalibratedSculkSensor(int meta) {
        super(meta);
    }

    public int getId() {
        return CALIBRATED_SCULK_SENSOR;
    }

    public String getName() {
        return "Calibrated Sculk Sensor";
    }
}