package cn.nukkit.block;

import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

public class BlockLitDeepslateRedstoneOre extends BlockDeepslateRedstoneOre implements IBlockOreRedstoneGlowing {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIT_DEEPSLATE_REDSTONE_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLitDeepslateRedstoneOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLitDeepslateRedstoneOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Glowing Deepslate Redstone Ore";
    }

    @Override
    public int getLightLevel() {
        return 9;
    }

    @Override
    public Item toItem() {
        return IBlockOreRedstoneGlowing.super.toItem();
    }

    @Override
    public int onUpdate(int type) {
        return IBlockOreRedstoneGlowing.super.onUpdate(this, type);
    }
}