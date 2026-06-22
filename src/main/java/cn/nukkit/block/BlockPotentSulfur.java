package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.ItemTool;
import cn.nukkit.blockentity.BlockEntityID;
import cn.nukkit.blockentity.BlockEntityPotentSulfur;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.POTENT_SULFUR_STATE;

public class BlockPotentSulfur extends BlockSolid implements BlockEntityHolder<BlockEntityPotentSulfur> {
    public static final BlockProperties PROPERTIES = new BlockProperties(POTENT_SULFUR, POTENT_SULFUR_STATE);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPotentSulfur() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPotentSulfur(BlockState blockstate) {
        super(blockstate);
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
    public double getHardness() {
        return 1.5;
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
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
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
