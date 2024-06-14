package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType2;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.STONE_SLAB_TYPE_2;

public class BlockDoubleStoneBlockSlab2 extends BlockDoubleSlabBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(DOUBLE_STONE_BLOCK_SLAB2,
            CommonBlockProperties.MINECRAFT_VERTICAL_HALF, STONE_SLAB_TYPE_2);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDoubleStoneBlockSlab2() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDoubleStoneBlockSlab2(BlockState blockstate) {
        super(blockstate);
    }

    public StoneSlabType2 getSlabType() {
        return getPropertyValue(STONE_SLAB_TYPE_2);
    }

    public void setSlabType(StoneSlabType2 type) {
        setPropertyValue(STONE_SLAB_TYPE_2, type);
    }

    @Override
    public String getSlabName() {
        return getSlabType().name();
    }

    @Override
    public double getResistance() {
        return 30;
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
        return BlockStoneBlockSlab2.PROPERTIES.getDefaultState();
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
        Block block = Block.get(getSingleSlab()).setPropertyValues(CommonBlockProperties.STONE_SLAB_TYPE_2.createValue(getSlabType()));
        return new ItemBlock(block);
    }
}