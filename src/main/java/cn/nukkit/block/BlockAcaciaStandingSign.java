package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemAcaciaSign;
import org.jetbrains.annotations.NotNull;

public class BlockAcaciaStandingSign extends BlockStandingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(ACACIA_STANDING_SIGN, CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAcaciaStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAcaciaStandingSign(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected String getStandingSignId() {
        return PROPERTIES.getIdentifier();
    }

    @Override
    public String getWallSignId() {
        return BlockAcaciaWallSign.PROPERTIES.getIdentifier();
    }

    @Override
    public Item toItem() {
        return new ItemAcaciaSign();
    }
}