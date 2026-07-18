package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Class for End Rod block.
 *
 * @author PikyCZ
 * @see <a href="https://minecraft.wiki/w/End_Rod">Minecraft wiki</a>
 */
public class BlockEndRod extends BlockTransparent implements Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(END_ROD, CommonBlockProperties.FACING_DIRECTION);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(0)
            .resistance(0)
            .toolType(ItemTool.TYPE_PICKAXE)
            .lightEmission(14)
            .canBeFlowedInto(false)
            .waterloggingLevel(2)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockEndRod() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockEndRod(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "End Rod";
    }

    @Override
    public double getMinX() {
        return this.x + 0.4;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.4;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.6;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.6;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        int[] faces = {0, 1, 3, 2, 5, 4};
        setPropertyValue(CommonBlockProperties.FACING_DIRECTION, faces[player != null ? face.getIndex() : 0]);
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getPropertyValue(CommonBlockProperties.FACING_DIRECTION) & 0x07);
    }
}
