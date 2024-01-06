package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBambooSign;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;


public class BlockBambooWallSign extends BlockWallSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO_WALL_SIGN, FACING_DIRECTION);

    public BlockBambooWallSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBambooWallSign(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public String getName() {
        return "Bamboo Wall Sign";
    }

    @Override
    public String getStandingSignId() {
        return BAMBOO_STANDING_SIGN;
    }

    @Override
    public String getWallSignId() {
        return BlockAcaciaWallSign.PROPERTIES.getIdentifier();
    }

    @Override
    public Item toItem() {
        return new ItemBambooSign();
    }
}