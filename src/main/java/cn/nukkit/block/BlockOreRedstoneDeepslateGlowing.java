package cn.nukkit.block;

import cn.nukkit.item.Item;

/**
 * @author joserobjr
 * @since 2021-06-13
 */


public class BlockOreRedstoneDeepslateGlowing extends BlockOreRedstoneDeepslate implements IBlockOreRedstoneGlowing {


    public BlockOreRedstoneDeepslateGlowing() {
        // Does nothing
    }

    @Override
    public int getId() {
        return LIT_DEEPSLATE_REDSTONE_ORE;
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
        return IBlockOreRedstoneGlowing.super.onUpdate(type);
    }
}
