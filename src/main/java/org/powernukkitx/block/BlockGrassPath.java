package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * @author xtypr
 * @since 2015/11/22
 */
public class BlockGrassPath extends BlockGrassBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties(GRASS_PATH);
    public static final BlockDefinition DEFINITION = BlockGrassBlock.DEFINITION.toBuilder()
            .hardness(0.65)
            .resistance(0.65)
            .isTransparent(true)
            .build();

    public BlockGrassPath() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockGrassPath(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Dirt Path";
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.up().isSolid()) {
                this.level.setBlock(this, Block.get(BlockID.DIRT), false, true);
            }

            return Level.BLOCK_UPDATE_NORMAL;
        }
        return 0;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isHoe()) {
            item.useOn(this);
            this.getLevel().setBlock(this, get(FARMLAND), true);
            if (player != null) {
                player.getLevel().addSound(player, Sound.USE_GRASS);
            }
            return true;
        }

        return false;
    }

    }
