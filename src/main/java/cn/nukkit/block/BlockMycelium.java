package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.DirtType;
import cn.nukkit.event.block.BlockSpreadEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;

/**
 * @author Pub4Game
 * @since 03.01.2016
 */
public class BlockMycelium extends BlockDirt {
    public static final BlockProperties PROPERTIES = new BlockProperties(MYCELIUM);

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
            if (getLevel().getFullLight(add(0, 1, 0)) >= BlockCrops.MINIMUM_LIGHT_LEVEL) {
                //TODO: light levels
                NukkitRandom random = new NukkitRandom();
                x = random.nextInt((int) x - 1, (int) x + 1);
                y = random.nextInt((int) y - 1, (int) y + 1);
                z = random.nextInt((int) z - 1, (int) z + 1);
                Block block = this.getLevel().getBlock(new Vector3(x, y, z));
                if (block.getId().equals(Block.DIRT) && block.getPropertyValue(CommonBlockProperties.DIRT_TYPE) == DirtType.NORMAL) {
                    if (block.up().isTransparent()) {
                        BlockSpreadEvent ev = new BlockSpreadEvent(block, this, Block.get(BlockID.MYCELIUM));
                        Server.getInstance().getPluginManager().callEvent(ev);
                        if (!ev.isCancelled()) {
                            this.getLevel().setBlock(block, ev.getNewState());
                        }
                    }
                }
            }
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
}
