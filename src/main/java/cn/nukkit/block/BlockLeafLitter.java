package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.GROWTH;
import static cn.nukkit.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;

/**
 * PowerNukkitX Project 2023/7/15
 *
 * @author daoge_cmd
 */
public class BlockLeafLitter extends BlockSegmented {

    public static final BlockProperties PROPERTIES = new BlockProperties(LEAF_LITTER, MINECRAFT_CARDINAL_DIRECTION, GROWTH);

    public BlockLeafLitter() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLeafLitter(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Leaf Litter";
    }

    @Override
    public boolean isSupportValid(Block block) {
        return block.isFullBlock();
    }
}
