package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySculkSensor;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

/**
 * @author LT_Name
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockSculkSensor extends BlockSolid implements BlockEntityHolder<BlockEntitySculkSensor> {

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
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null;
    }
}
