package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityBlastFurnace;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.jetbrains.annotations.NotNull;

public class BlockLitBlastFurnace extends BlockLitFurnace {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIT_BLAST_FURNACE, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLitBlastFurnace() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLitBlastFurnace(BlockState blockstate) {
        super(blockstate);
    }

    public BlockLitBlastFurnace(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    @Override
    public String getName() {
        return "Burning Blast Furnace";
    }

    @Override
    @NotNull public String getBlockEntityType() {
        return BlockEntity.BLAST_FURNACE;
    }

    @Override
    @NotNull public Class<? extends BlockEntityBlastFurnace> getBlockEntityClass() {
        return BlockEntityBlastFurnace.class;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockBlastFurnace());
    }
}