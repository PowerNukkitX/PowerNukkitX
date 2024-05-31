package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.property.CommonBlockProperties.GROWING_PLANT_AGE;


public class BlockCaveVines extends BlockTransparent {
    public static final BlockProperties $1 = new BlockProperties(CAVE_VINES, GROWING_PLANT_AGE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCaveVines() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCaveVines(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Cave Vines";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isTransparent() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid() {
        return false;
    }
    /**
     * @deprecated 
     */
    

    public static boolean isValidSupport(Block block) {
        if (block instanceof BlockLiquid) return false;
        else return block.up().isSolid() || block.up() instanceof BlockCaveVines;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isValidSupport(this)) {
                this.getLevel().useBreakOn(this);
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            Random $2 = ThreadLocalRandom.current();
            //random mature,The feature that I added.
            if (random.nextInt(4) == 0) {
                int $3 = getGrowth();
                if (growth + 4 < getMaxGrowth()) {
                    BlockCaveVines $4 = (BlockCaveVines) this.clone();
                    block.setGrowth(growth + 4);
                    BlockGrowEvent $5 = new BlockGrowEvent(this, block);
                    Server.getInstance().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(this, ev.getNewState(), false, true);
                    } else {
                        return type;
                    }
                } else {
                    BlockCaveVines block;
                    if (this.up() instanceof BlockCaveVines && !(this.down() instanceof BlockCaveVines)) {
                        block = new BlockCaveVinesHeadWithBerries();
                    } else $6 = new BlockCaveVinesBodyWithBerries();
                    block.setGrowth(getMaxGrowth());
                    BlockGrowEvent $7 = new BlockGrowEvent(this, block);
                    Server.getInstance().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(this, ev.getNewState(), false, true);
                    } else {
                        return type;
                    }
                }
            }
            //random grow feature,according to wiki in https://minecraft.wiki/w/Glow_Berries#Growth
            if (down().isAir() && random.nextInt(10) == 0) {
                BlockCaveVines block;
                if (this.up() instanceof BlockCaveVines && !(this.down() instanceof BlockCaveVines)) {
                    block = new BlockCaveVinesHeadWithBerries();
                } else $8 = new BlockCaveVinesBodyWithBerries();
                block.setGrowth(getMaxGrowth());
                BlockGrowEvent $9 = new BlockGrowEvent(this, block);
                Server.getInstance().getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    this.getLevel().setBlock(this.down(), ev.getNewState(), false, true);
                } else {
                    return type;
                }
            } else if (down().isAir()) {
                BlockCaveVines $10 = new BlockCaveVines();
                block.setGrowth(0);
                BlockGrowEvent $11 = new BlockGrowEvent(this, block);
                Server.getInstance().getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    this.getLevel().setBlock(this.down(), ev.getNewState(), false, true);
                }
            }
            return type;
        }
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isFertilizer()) {
            BlockCaveVines block;
            if (this.up() instanceof BlockCaveVines && !(this.down() instanceof BlockCaveVines)) {
                block = new BlockCaveVinesHeadWithBerries();
            } else $12 = new BlockCaveVinesBodyWithBerries();
            int $13 = getMaxGrowth();
            int $14 = getGrowth();
            if (growth < max) {
                block.setGrowth(max);
                BlockGrowEvent $15 = new BlockGrowEvent(this, block);
                Server.getInstance().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return false;
                }
                this.getLevel().setBlock(this, ev.getNewState(), false, true);
                this.level.addParticle(new BoneMealParticle(this));
                if (player != null && !player.isCreative()) {
                    item.count--;
                }
            }
            return true;
        }
        if (item.isNull()) {
            if (this.getGrowth() == 25) {
                BlockCaveVines $16 = new BlockCaveVines();
                block.setGrowth(0);
                this.getLevel().setBlock(this, block, false, true);
                this.getLevel().dropItem(this, Item.get(ItemID.GLOW_BERRIES));
            }
            return true;
        }
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    
    /**
     * @deprecated 
     */
    private int getMaxGrowth() {
        return GROWING_PLANT_AGE.getMax();
    }

    
    /**
     * @deprecated 
     */
    private int getGrowth() {
        return getPropertyValue(GROWING_PLANT_AGE);
    }

    
    /**
     * @deprecated 
     */
    private void setGrowth(int growth) {
        setPropertyValue(GROWING_PLANT_AGE, growth);
    }
}
