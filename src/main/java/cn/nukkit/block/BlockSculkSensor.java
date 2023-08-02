package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySculkSensor;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

/**
 * @author LT_Name
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockSculkSensor extends BlockSolid implements BlockEntityHolder<BlockEntitySculkSensor>, RedstoneComponent {

    public static final IntBlockProperty SCULK_SENSOR_PHASE = new IntBlockProperty("sculk_sensor_phase", false, 2);
    public static final BlockProperties PROPERTIES = new BlockProperties(SCULK_SENSOR_PHASE);

    @Override
    public String getName() {
        return "Sculk Sensor";
    }

    @Override
    public int getId() {
        return SCULK_SENSOR;
    }

    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @NotNull
    @Override
    public Class<? extends BlockEntitySculkSensor> getBlockEntityClass() {
        return BlockEntitySculkSensor.class;
    }

    @NotNull
    @Override
    public String getBlockEntityType() {
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
            if (level.getServer().isRedstoneEnabled()) {
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
        this.setIntValue(SCULK_SENSOR_PHASE, phase);
        this.level.setBlock(this, this, true, false);
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

}
