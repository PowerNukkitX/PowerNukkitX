package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

public class BlockBeeNest extends BlockBeehive {
    public static final BlockProperties PROPERTIES = new BlockProperties(BEE_NEST, CommonBlockProperties.DIRECTION, CommonBlockProperties.HONEY_LEVEL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBeeNest() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBeeNest(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Bee Nest";
    }

    @Override
    public int getBurnChance() {
        return 30;
    }

    @Override
    public int getBurnAbility() {
        return 60;
    }

    @Override
    public double getHardness() {
        return 0.3;
    }

    @Override
    public double getResistance() {
        return 1.5;
    }
}
