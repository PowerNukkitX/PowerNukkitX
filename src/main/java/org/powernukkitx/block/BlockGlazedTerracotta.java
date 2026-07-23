package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.FACING_DIRECTION;

/**
 * @author CreeperFace
 * @since 2.6.2017
 */
public abstract class BlockGlazedTerracotta extends BlockSolid implements Faceable {
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(1.4)
            .resistance(7)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canBePushed(true)
            .canBePulled(false)
            .sticksToPiston(false)
            .build();
    public BlockGlazedTerracotta(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockGlazedTerracotta(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        int[] faces = {2, 5, 3, 4};
        this.setBlockFace(BlockFace.fromIndex(faces[player != null ? player.getDirection().getHorizontalIndex() : 0]));
        return this.getLevel().setBlock(block, this, true, true);
    }

    
    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getPropertyValue(FACING_DIRECTION));
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(FACING_DIRECTION, face.getIndex());
    }

    //Glazed clay tiles can be pushed but cannot be pulled back.
    //see: https://minecraft.wiki/w/Glazed_Terracotta#Usage
    
    
    }
