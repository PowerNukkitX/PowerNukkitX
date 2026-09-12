package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemStrawBed;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.DIRECTION;
import static org.powernukkitx.block.property.CommonBlockProperties.HEAD_PIECE_BIT;
import static org.powernukkitx.block.property.CommonBlockProperties.OCCUPIED_BIT;

/**
 * A single-use bed that is destroyed after a player wakes up.
 */
public class BlockStrawBed extends BlockBed {
    public static final BlockProperties PROPERTIES = new BlockProperties(STRAW_BED, DIRECTION, HEAD_PIECE_BIT, OCCUPIED_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrawBed() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStrawBed(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean setsRespawnPoint() {
        return false;
    }

    @Override
    public String getName() {
        return "Straw Bed";
    }

    @Override
    public void onSleepEnd(@NotNull Player player) {
        if (level != null && level.getBlock(this).getId().equals(getId())) {
            onBreak(Item.AIR);
        }
    }

    @Override
    public Item toItem() {
        return new ItemStrawBed();
    }
}
