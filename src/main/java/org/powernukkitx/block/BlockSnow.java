package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemSnowball;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockSnow extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(SNOW);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0.6)
            .resistance(1)
            .toolType(ItemTool.TYPE_SHOVEL)
            .canBeActivated(true)
            .canSilkTouch(true)
            .canHarvestWithHand(false)
            .build();

    public BlockSnow() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockSnow(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Snow";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShovel() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    new ItemSnowball(0, 4)
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isShovel()) {
            item.useOn(this);
            this.level.useBreakOn(this, item.clone().clearNamedTag(), null, true);
            return true;
        }
        return false;
    }
}
