package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.projectile.EntitySmallFireball;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Position;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.jetbrains.annotations.NotNull;


public class BlockPowderSnow extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(POWDER_SNOW);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(0.25)
            .resistance(0.1)
            .canPassThrough(true)
            .isTransparent(true)
            .isSolid(false)
            .build();

    public BlockPowderSnow() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockPowderSnow(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Powder Snow";
    }

    
    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    public boolean onProjectileHit(@NotNull Entity projectile, @NotNull Position position, @NotNull Vector3 motion) {
        if (projectile instanceof EntitySmallFireball) {
            this.getLevel().useBreakOn(this);
            return true;
        }
        return false;
    }
}
