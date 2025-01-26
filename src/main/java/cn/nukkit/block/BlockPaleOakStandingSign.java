package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemAcaciaSign;
import cn.nukkit.item.ItemPaleOakSign;
import org.jetbrains.annotations.NotNull;

public class BlockPaleOakStandingSign extends BlockStandingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(PALE_OAK_STANDING_SIGN, CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPaleOakStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPaleOakStandingSign(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected String getStandingSignId() {
        return PROPERTIES.getIdentifier();
    }

    @Override
    public String getWallSignId() {
        return BlockPaleOakWallSign.PROPERTIES.getIdentifier();
    }

    @Override
    public Item toItem() {
        return new ItemPaleOakSign();
    }
}