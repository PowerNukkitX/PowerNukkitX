package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntitySmoker;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.jetbrains.annotations.NotNull;

public class BlockLitSmoker extends BlockLitFurnace {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIT_SMOKER, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLitSmoker() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLitSmoker(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Burning Smoker";
    }

    @Override
    @NotNull public String getBlockEntityType() {
        return BlockEntity.SMOKER;
    }

    @Override
    @NotNull public Class<? extends BlockEntitySmoker> getBlockEntityClass() {
        return BlockEntitySmoker.class;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockSmoker());
    }
}