package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockBlackstoneStairs extends BlockStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:blackstone_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlackstoneStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlackstoneStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Blackstone Stairs";
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}