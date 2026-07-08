package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockBush extends BlockFlowable implements Supportable {

    public static final BlockProperties PROPERTIES = new BlockProperties(BUSH);

    public BlockBush() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBush(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public int getSnowloggingLevel() {
        return 1;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (isSupportDirt(down())) {
            this.getLevel().setBlock(block, this, true);
            return true;
        }
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears() || item.hasEnchantment(Enchantment.ID_SILK_TOUCH)) {
            return new Item[]{
                    toItem()
            };
        }

        return Item.EMPTY_ARRAY;
    }

}
