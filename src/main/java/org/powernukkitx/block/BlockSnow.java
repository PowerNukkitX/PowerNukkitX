package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemSnowball;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockSnow extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(SNOW);

    public BlockSnow() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockSnow(BlockState blockState) {
        super(blockState);
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
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 1;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
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
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
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
