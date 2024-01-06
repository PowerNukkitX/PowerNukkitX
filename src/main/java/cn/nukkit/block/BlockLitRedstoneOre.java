package cn.nukkit.block;

import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

public class BlockLitRedstoneOre extends BlockRedstoneOre implements IBlockOreRedstoneGlowing {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIT_REDSTONE_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLitRedstoneOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLitRedstoneOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Glowing Redstone Ore";
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