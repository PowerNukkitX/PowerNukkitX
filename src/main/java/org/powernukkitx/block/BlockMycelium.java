package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.event.block.BlockFadeEvent;
import org.powernukkitx.event.block.BlockSpreadEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Pub4Game
 * @since 03.01.2016
 */
public class BlockMycelium extends BlockDirt {
    public static final BlockProperties PROPERTIES = new BlockProperties(MYCELIUM);

    public static final int MINIMUM_SPREAD_LIGHT_LEVEL = 4;
    public static final int MAXIMUM_SPREAD_LIGHT_FILTER = 2;

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMycelium() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockMycelium(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Mycelium";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                new ItemBlock(Block.get(BlockID.DIRT))
        };
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (up().getLightFilter() > 1) {
                BlockFadeEvent ev = new BlockFadeEvent(this, Block.get(BlockID.DIRT));
                Server.getInstance().getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    this.getLevel().setBlock(this, ev.getNewState());
                    return type;
                }
            }

            if (getLevel().getFullLight(add(0, 1, 0)) >= BlockCrops.MINIMUM_LIGHT_LEVEL) {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                int x = random.nextInt((int) this.x - 1, (int) this.x + 1 + 1);
                int y = random.nextInt((int) this.y - 3, (int) this.y + 1 + 1);
                int z = random.nextInt((int) this.z - 1, (int) this.z + 1 + 1);
                Block block = this.getLevel().getBlock(new Vector3(x, y, z));
                if (block.getId().equals(BlockID.DIRT) && getLevel().getFullLight(block.up()) >= MINIMUM_SPREAD_LIGHT_LEVEL && block.up().getLightFilter() < MAXIMUM_SPREAD_LIGHT_FILTER) {
                    BlockSpreadEvent ev = new BlockSpreadEvent(block, this, Block.get(BlockID.MYCELIUM));
                    Server.getInstance().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(block, ev.getNewState());
                    }
                }
            }
            return type;
        }
        return 0;
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
            if (up().isAir()) {
                item.useOn(this);
                this.getLevel().setBlock(this, Block.get(BlockID.GRASS_PATH));
                if (player != null) {
                    player.getLevel().addSound(player, Sound.USE_GRASS);
                }
                return true;
            }
        }
        return false;
    }
}
