package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import org.jetbrains.annotations.NotNull;

public class BlockInvisibleBedrock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:invisible_bedrock");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockInvisibleBedrock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockInvisibleBedrock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Invisible Bedrock";
    }

    @Override
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public double getResistance() {
        return 18000000;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public  boolean canBePulled() {
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.AIR));
    }
}