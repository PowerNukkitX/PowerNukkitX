package cn.nukkit.block.impl;

import cn.nukkit.block.BlockTransparentMeta;
import cn.nukkit.block.property.BlockProperties;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.IntBlockProperty;

public class BlockCalibratedSculkSensor extends BlockTransparentMeta {
    public static final IntBlockProperty SCULK_SENSOR_PHASE = new IntBlockProperty("sculk_sensor_phase", false, 2);

    public static final BlockProperties PROPERTIES =
            new BlockProperties(CommonBlockProperties.CARDINAL_DIRECTION, SCULK_SENSOR_PHASE);

    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCalibratedSculkSensor() {}

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
