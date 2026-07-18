package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * @author xtypr
 * @since 2015/11/22
 */
public class BlockPodzol extends BlockDirt {
    public static final BlockProperties PROPERTIES = new BlockProperties(PODZOL);
    public static final BlockDefinition DEFINITION = BlockDirt.DEFINITION.toBuilder()
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPodzol() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockPodzol(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Podzol";
    }

    
    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (!this.up().canBeReplaced()) {
            return false;
        }

        if (item.isShovel()) {
            item.useOn(this);
            this.getLevel().setBlock(this, Block.get(BlockID.GRASS_PATH));
            if (player != null) {
                player.getLevel().addSound(player, Sound.USE_GRASS);
            }
            return true;
        }
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.hasEnchantment(Enchantment.ID_SILK_TOUCH)) {
            return new Item[]{
                    this.toItem()
            };
        }

        return new Item[]{
                Block.get(BlockID.DIRT).toItem()
        };
    }

}
