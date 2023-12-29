package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBambooTrapdoor extends BlockTrapdoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO_TRAPDOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPSIDE_DOWN_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBambooTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBambooTrapdoor(BlockState blockstate) {
        super(blockstate);
    }

    public String getName() {
        return "Bamboo Trapdoor";
    }
}