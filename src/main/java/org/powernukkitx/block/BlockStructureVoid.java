package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author good777LUCKY
 */
public class BlockStructureVoid extends BlockSolid {

    public static final BlockProperties PROPERTIES = new BlockProperties(STRUCTURE_VOID);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0)
            .resistance(0)
            .canPassThrough(true)
            .canBePushed(false)
            .canBePulled(false)
            .canHarvestWithHand(false)
            .isSolid(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStructureVoid() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStructureVoid(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Structure Void";
    }

    
    @Override
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return player != null && player.isCreative();
    }
    
    
    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }
}
