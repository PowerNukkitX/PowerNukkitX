package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class BlockDoubleStoneBlockSlab extends BlockDoubleSlabBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(DOUBLE_STONE_BLOCK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.STONE_SLAB_TYPE);

    public static BlockState getDoubleBlockState(String string) {
        return switch (string) {
            case "quartz" -> BlockQuartzSlab.PROPERTIES.getDefaultState();
            case "wood" -> BlockPetrifiedOakSlab.PROPERTIES.getDefaultState();
            case "stone_brick" -> BlockStoneBrickSlab.PROPERTIES.getDefaultState();
            case "brick" -> BlockBrickSlab.PROPERTIES.getDefaultState();
            case "smooth_stone" -> BlockSmoothStoneSlab.PROPERTIES.getDefaultState();
            case "sandstone" -> BlockSandstoneSlab.PROPERTIES.getDefaultState();
            case "nether_brick" -> BlockNetherBrickSlab.PROPERTIES.getDefaultState();
            default -> BlockCobblestoneSlab.PROPERTIES.getDefaultState();
        };
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDoubleStoneBlockSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDoubleStoneBlockSlab(BlockState blockstate) {
        super(blockstate);
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

    public StoneSlabType getSlabType() {
        return getPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE);
    }

    @Override
    public BlockState getSingleSlab() {
        return getDoubleBlockState(getSlabType().name().toLowerCase(Locale.ENGLISH));
    }

    public void setSlabType(StoneSlabType type) {
        setPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE, type);
    }

    @Override
    public String getSlabName() {
        return getSlabType().name();
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
        Block block = Block.get(getSingleSlab());
        return new ItemBlock(block);
    }
}