package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPaleOakTrapdoor extends BlockTrapdoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(PALE_OAK_TRAPDOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPSIDE_DOWN_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPaleOakTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPaleOakTrapdoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Pale Oak Trapdoor";
    }

}