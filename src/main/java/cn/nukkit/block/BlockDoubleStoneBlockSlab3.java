package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType3;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockDoubleStoneBlockSlab3 extends BlockDoubleSlabBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(DOUBLE_STONE_BLOCK_SLAB3, CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.STONE_SLAB_TYPE_3);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDoubleStoneBlockSlab3() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDoubleStoneBlockSlab3(BlockState blockstate) {
        super(blockstate);
    }

    public StoneSlabType3 getSlabType() {
        return getPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE_3);
    }

    public void setSlabType(StoneSlabType3 type) {
        setPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE_3, type);
    }

    @Override
    public String getSlabName() {
        return getSlabType().name();
    }

    @Override
    public double getResistance() {
        return getToolType() > ItemTool.TIER_WOODEN ? 30 : 15;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockStoneBlockSlab3.PROPERTIES.getDefaultState();
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item toItem() {
        Block block = Block.get(getSingleSlab()).setPropertyValues(CommonBlockProperties.STONE_SLAB_TYPE_3.createValue(getSlabType()));
        return new ItemBlock(block);
    }

}