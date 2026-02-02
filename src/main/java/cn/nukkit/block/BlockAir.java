package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.definition.BlockDefinition;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockAir extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(AIR);
    public static final BlockState STATE = PROPERTIES.getDefaultState();
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .canPassThrough(true)
            .canBeFlowedInto(true)
            .canBePlaced(false)
            .canBeReplaced(true)
            .isSolid(false)
            .hardness(0)
            .resistance(0)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAir() {
        super(STATE, DEFINITION);
    }

    public BlockAir(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Air";
    }

    @Override
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return false;
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

}
