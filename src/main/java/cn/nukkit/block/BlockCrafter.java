package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCrafter;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockCrafter extends BlockSolid implements BlockEntityHolder<BlockEntityCrafter> {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRAFTER, CommonBlockProperties.CRAFTING, CommonBlockProperties.ORIENTATION, CommonBlockProperties.TRIGGERED_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrafter() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrafter(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityCrafter> getBlockEntityClass() {
        return BlockEntityCrafter.class;
    }

    @Override
    public @NotNull String getBlockEntityType() {
        return BlockEntity.CRAFTER;
    }

    @Override
    public String getName() {
        return "Crafter";
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getResistance() {
        return 1.5D;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (isNotActivate(player)) return false;

        BlockEntityCrafter crafter = this.getOrCreateBlockEntity();
        player.addWindow(crafter.getInventory());
        return true;
    }
}