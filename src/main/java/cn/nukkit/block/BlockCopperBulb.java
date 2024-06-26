package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OxidizationLevel;
import cn.nukkit.blockentity.BlockEntityHopper;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.level.Level;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

public class BlockCopperBulb extends BlockCopperBulbBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(COPPER_BULB, CommonBlockProperties.LIT, CommonBlockProperties.POWERED_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperBulb() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperBulb(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }

    @Override
    public int getLightLevel() {
        return getLit() ? 15 : 0;
    }
}