package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Pub4Game
 * @since 2015/12/26
 */
public class BlockNetherrack extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(NETHERRACK);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0.4)
            .resistance(0.4)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canBeActivated(true)
            .isFertilizable(true)
            .build();

    public BlockNetherrack() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockNetherrack(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Netherrack";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    
    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isNull() || !item.isFertilizer() || up().getId() != AIR) {
            return false;
        }

        List<String> options = new ArrayList<String>();
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            String id = getSide(face).getId();
            if ((id.equals(CRIMSON_NYLIUM) || id.equals(WARPED_NYLIUM)) && !options.contains(id)) {
                options.add(id);
            }
        }
        
        String nylium;
        int size = options.size();
        if (size == 0) {
            return false;
        } else if (size == 1) {
            nylium = options.get(0);
        } else {
            nylium = options.get(ThreadLocalRandom.current().nextInt(size));
        }
        
        if (level.setBlock(this, Block.get(nylium), true)) {
            if (player == null || !player.isCreative()) {
                item.count--;
            }
            return true;
        }

        return false;
    }

    
    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    }
