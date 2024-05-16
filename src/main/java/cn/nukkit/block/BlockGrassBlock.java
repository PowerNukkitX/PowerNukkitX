package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.DirtType;
import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.event.block.BlockSpreadEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.legacytree.LegacyTallGrass;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

public class BlockGrassBlock extends BlockDirt {
    public static final BlockProperties PROPERTIES = new BlockProperties(GRASS_BLOCK);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrassBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public double getResistance() {
        return 0.6;
    }

    @Override
    public String getName() {
        return "Grass Block";
    }

    @Override
    @NotNull
    public DirtType getDirtType() {
        return DirtType.NORMAL;
    }

    @Override
    public void setDirtType(@Nullable DirtType dirtType) throws Exception {
        if (dirtType != null) {
            throw new Exception(getName() + "don't support DirtType!");
        }
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (!this.up().canBeReplaced()) {
            return false;
        }

        if (item.isFertilizer()) {
            if (player != null && (player.gamemode & 0x01) == 0) {
                item.count--;
            }
            this.level.addParticle(new BoneMealParticle(this));
            BlockManager blockManager = new BlockManager(this.level);
            LegacyTallGrass.growGrass(blockManager, this, new NukkitRandom());
            blockManager.applyBlockUpdate();
            return true;
        } else if (item.isHoe()) {
            item.useOn(this);
            this.getLevel().setBlock(this, Block.get(BlockID.FARMLAND));
            if (player != null) {
                player.getLevel().addSound(player, Sound.USE_GRASS);
            }
            return true;
        } else if (item.isShovel()) {
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
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            // Grass dies and changes to dirt after a random time (when a random tick lands on the block)
            // if directly covered by any opaque block.
            // Transparent blocks can kill grass in a similar manner,
            // but only if they cause the light level above the grass block to be four or below (like water does),
            // and the surrounding area is not otherwise sufficiently lit up.
            if (up().getLightFilter() > 1) {
                BlockFadeEvent ev = new BlockFadeEvent(this, Block.get(BlockID.DIRT));
                Server.getInstance().getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    this.getLevel().setBlock(this, ev.getNewState());
                    return type;
                }
            }

            // Grass can spread to nearby dirt blocks.
            // Grass spreading without player intervention depends heavily on the time of day.
            // For a dirt block to accept grass from a nearby grass block, the following requirements must be met:

            // The source block must have a light level of 9 or brighter directly above it.
            if (getLevel().getFullLight(add(0, 1, 0)) >= BlockCrops.MINIMUM_LIGHT_LEVEL) {

                // The dirt block receiving grass must be within a 3×5×3 range of the source block
                // where the source block is in the center of the second topmost layer of that range.
                ThreadLocalRandom random = ThreadLocalRandom.current();
                int x = random.nextInt((int) this.x - 1, (int) this.x + 1 + 1);
                int y = random.nextInt((int) this.y - 3, (int) this.y + 1 + 1);
                int z = random.nextInt((int) this.z - 1, (int) this.z + 1 + 1);
                Block block = this.getLevel().getBlock(new Vector3(x, y, z));
                if (block.getId().equals(Block.DIRT)
                        // It cannot spread to coarse dirt
                        && block.getPropertyValue(CommonBlockProperties.DIRT_TYPE) == DirtType.NORMAL

                        // The dirt block must have a light level of at least 4 above it.
                        && getLevel().getFullLight(block) >= 4

                        // Any block directly above the dirt block must not reduce light by 2 levels or more.
                        && block.up().getLightFilter() < 2) {
                    BlockSpreadEvent ev = new BlockSpreadEvent(block, this, Block.get(BlockID.GRASS_BLOCK));
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
    public Item[] getDrops(Item item) {
        return new Item[]{Item.get(DIRT)};
    }
}