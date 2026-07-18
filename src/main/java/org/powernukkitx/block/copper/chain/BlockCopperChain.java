package org.powernukkitx.block.copper.chain;

import org.powernukkitx.block.definition.BlockDefinition;

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
    public static final BlockDefinition DEFINITION = AbstractBlockCopperChain.DEFINITION.toBuilder()
            .hardness(5)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .waterloggingLevel(1)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperChain() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperChain(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockCopperChain(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    @Override
    public String getName() {
        return "Copper Chain";
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }
}
