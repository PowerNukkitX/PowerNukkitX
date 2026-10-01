package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntitySculkCatalyst;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockSculkCatalyst extends BlockSolid implements BlockEntityHolder<BlockEntitySculkCatalyst> {
    public static final BlockProperties PROPERTIES = new BlockProperties(SCULK_CATALYST, CommonBlockProperties.BLOOM);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(3.0)
            .resistance(3)
            .toolType(ItemTool.TYPE_HOE)
            .lightEmission(6)
            .canBePushed(false)
            .canBePulled(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSculkCatalyst() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSculkCatalyst(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Sculk Catalyst";
    }

    @Override
    @NotNull public Class<? extends BlockEntitySculkCatalyst> getBlockEntityClass() {
        return BlockEntitySculkCatalyst.class;
    }

    @Override
    @NotNull public String getBlockEntityType() {
        return BlockEntity.SCULK_CATALYST;
    }

}
