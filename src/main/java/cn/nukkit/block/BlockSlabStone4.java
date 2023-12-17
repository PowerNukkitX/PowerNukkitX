package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockproperty.value.StoneSlab4Type;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockSlabStone4 extends BlockSlab {


    public static final BlockProperties PROPERTIES = new BlockProperties(
            StoneSlab4Type.PROPERTY,
            CommonBlockProperties.VERTICAL_HALF
    );

    public static final int MOSSY_STONE_BRICKS = 0;
    public static final int SMOOTH_QUARTZ = 1;
    public static final int STONE = 2;
    public static final int CUT_SANDSTONE = 3;
    public static final int CUT_RED_SANDSTONE = 4;


    public BlockSlabStone4() {
        this(0);
    }


    public BlockSlabStone4(int meta) {
        super(meta, DOUBLE_STONE_SLAB4);
    }

    @Override
    public int getId() {
        return STONE_SLAB4;
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }


    public StoneSlab4Type getSlabType() {
        return getPropertyValue(StoneSlab4Type.PROPERTY);
    }


    public void setSlabType(StoneSlab4Type type) {
        setPropertyValue(StoneSlab4Type.PROPERTY, type);
    }


    @Override
    public String getSlabName() {
        return getSlabType().getEnglishName();
    }


    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId() == getId() && getSlabType().equals(slab.getPropertyValue(StoneSlab4Type.PROPERTY));
    }


    @Override

    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
