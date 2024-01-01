package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBirchTrapdoor extends BlockTrapdoor {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:birch_trapdoor", CommonBlockProperties.DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPSIDE_DOWN_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBirchTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBirchTrapdoor(BlockState blockstate) {
        super(blockstate);
    }


    @Override
    public String getName() {
        return "Birch Trapdoor";
    }
}