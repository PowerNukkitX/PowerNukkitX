package org.powernukkitx.block.copper.lantern;

import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.OxidizationLevel;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.jetbrains.annotations.NotNull;

/**
 * @author keksdev
 * @since 1.21.110
 */
public class BlockCopperLantern extends AbstractBlockCopperLantern {
    public static final BlockProperties PROPERTIES = new BlockProperties(COPPER_LANTERN, CommonBlockProperties.HANGING);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperLantern() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperLantern(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Copper Lantern";
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
