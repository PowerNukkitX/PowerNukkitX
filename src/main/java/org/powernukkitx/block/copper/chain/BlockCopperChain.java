package org.powernukkitx.block.copper.chain;

import org.powernukkitx.block.*;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.OxidizationLevel;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;


/**
 * @author keksdev
 * @since 1.21.110
 */
public class BlockCopperChain extends AbstractBlockCopperChain {
    public static final BlockProperties PROPERTIES = new BlockProperties(COPPER_CHAIN, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperChain() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperChain(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Copper Chain";
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
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
    public Item toItem() {
        return new ItemBlock(this, 0);
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
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }
}
