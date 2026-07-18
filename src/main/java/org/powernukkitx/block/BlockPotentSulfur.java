package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.blockentity.BlockEntityID;
import org.powernukkitx.blockentity.BlockEntityPotentSulfur;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.POTENT_SULFUR_STATE;

public class BlockPotentSulfur extends BlockSolid implements BlockEntityHolder<BlockEntityPotentSulfur> {
    public static final BlockProperties PROPERTIES = new BlockProperties(POTENT_SULFUR, POTENT_SULFUR_STATE);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(1.5)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPotentSulfur() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPotentSulfur(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        BlockEntityPotentSulfur entity = BlockEntityHolder.setBlockAndCreateEntity(this, false, true);
        if (entity != null) {
            entity.scheduleUpdate();
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        int result = super.onUpdate(type);
        if (this.level != null) {
            getOrCreateBlockEntity().scheduleUpdate();
        }
        return result;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public @NotNull Class<? extends BlockEntityPotentSulfur> getBlockEntityClass() {
        return BlockEntityPotentSulfur.class;
    }

    @Override
    public @NotNull String getBlockEntityType() {
        return BlockEntityID.POTENT_SULFUR;
    }
}
