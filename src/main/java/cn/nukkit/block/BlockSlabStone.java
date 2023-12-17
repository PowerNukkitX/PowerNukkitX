package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockproperty.value.StoneSlab1Type;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author CreeperFace
 * @since 26. 11. 2016
 */
public class BlockSlabStone extends BlockSlab {


    public static final BlockProperties PROPERTIES = new BlockProperties(
            StoneSlab1Type.PROPERTY,
            CommonBlockProperties.VERTICAL_HALF
    );

    public static final int STONE = 0;
    public static final int SANDSTONE = 1;
    public static final int WOODEN = 2;
    public static final int COBBLESTONE = 3;
    public static final int BRICK = 4;
    public static final int STONE_BRICK = 5;
    public static final int QUARTZ = 6;
    public static final int NETHER_BRICK = 7;

    public BlockSlabStone() {
        this(0);
    }

    public BlockSlabStone(int meta) {
        super(meta, DOUBLE_STONE_SLAB);
    }

    @Override
    public int getId() {
        return STONE_SLAB;
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }


    @Override
    public String getSlabName() {
        return getSlabType().getEnglishName();
    }


    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId() == getId() && getSlabType().equals(slab.getPropertyValue(StoneSlab1Type.PROPERTY));
    }

    @Override

    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }


    public StoneSlab1Type getSlabType() {
        return getPropertyValue(StoneSlab1Type.PROPERTY);
    }


    public void setSlabType(StoneSlab1Type type) {
        setPropertyValue(StoneSlab1Type.PROPERTY, type);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
