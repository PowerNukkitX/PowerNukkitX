package cn.nukkit.block;

import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.INFINIBURN_BIT;

/**
 * @author Angelic47 (Nukkit Project)
 * @apiNote Extends BlockSolidMeta instead of BlockSolid only in PowerNukkit
 */

public class BlockBedrock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:bedrock", INFINIBURN_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBedrock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBedrock(BlockState blockstate) {
        super(blockstate);
    }


    public boolean getBurnIndefinitely() {
        return getPropertyValue(INFINIBURN_BIT);
    }


    public void setBurnIndefinitely(boolean infiniburn) {
        setPropertyValue(INFINIBURN_BIT, infiniburn);
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
    public String getName() {
        return "Bedrock";
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

    public boolean canBePulled() {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
