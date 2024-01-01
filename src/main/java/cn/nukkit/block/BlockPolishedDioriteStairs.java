package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedDioriteStairs extends BlockStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:polished_diorite_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedDioriteStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedDioriteStairs(BlockState blockstate) {
        super(blockstate);
    }


    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Polished Diorite Stairs";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}