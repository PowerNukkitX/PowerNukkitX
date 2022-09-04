package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySculkSensor;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

/**
 * @author LT_Name
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockSculkSensor extends BlockSolid implements BlockEntityHolder<BlockEntitySculkSensor>, RedstoneComponent {

    public static final BooleanBlockProperty POWERED_BIT = new BooleanBlockProperty("powered_bit", false);
    public static final BlockProperties PROPERTIES = new BlockProperties(POWERED_BIT);

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

    @Nonnull
    @Override
    public Class<? extends BlockEntitySculkSensor> getBlockEntityClass() {
        return BlockEntitySculkSensor.class;
    }

    @Nonnull
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
            if (level.getServer().isRedstoneEnabled()){
                this.getBlockEntity().calPower();
                this.setPowered(false);
                updateAroundRedstone();
            }
            return type;
        }
        return 0;
    }

    public void setPowered(boolean powered) {
        if (powered) this.level.addSound(this.add(0.5,0.5,0.5), Sound.POWER_ON_SCULK_SENSOR);
        else this.level.addSound(this.add(0.5,0.5,0.5), Sound.POWER_OFF_SCULK_SENSOR);
        this.setBooleanValue(POWERED_BIT, powered);
        this.level.setBlock(this, this, true, false);
    }
}
