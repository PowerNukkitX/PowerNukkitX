package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.definition.BlockDefinition;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockAllow extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(ALLOW);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(-1)
            .resistance(18000000)
            .canBePushed(false)
            .canBePulled(false)
            .canHarvestWithHand(false)
            .build();

    public BlockAllow() {
        super(PROPERTIES.getDefaultState(), DEFINITION);
    }

    public BlockAllow(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Allow";
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (player != null && (!player.isCreative() || !player.isOp())) {
            return false;
        }

        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        if (player != null && (!player.isCreative() || !player.isOp())) {
            return false;
        }

        return super.isBreakable(vector, layer, face, item, player);
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }
}
