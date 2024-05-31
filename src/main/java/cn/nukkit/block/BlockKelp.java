package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.property.CommonBlockProperties.KELP_AGE;


public class BlockKelp extends BlockFlowable {
    public static final BlockProperties $1 = new BlockProperties(KELP, KELP_AGE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockKelp() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockKelp(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Kelp";
    }
    /**
     * @deprecated 
     */
    

    public int getAge() {
        return getPropertyValue(KELP_AGE);
    }
    /**
     * @deprecated 
     */
    

    public void setAge(int age) {
        setPropertyValue(KELP_AGE, age);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        Block $2 = down();
        Block $3 = block.getLevelBlockAtLayer(1);
        if ((down.getId().equals(KELP) || down.isSolid()) && !down.getId().equals(MAGMA) && !down.getId().equals(ICE) && !down.getId().equals(SOUL_SAND) &&
                (layer1Block instanceof BlockFlowingWater flowingWater && flowingWater.isSourceOrFlowingDown())
        ) {
            if (((BlockFlowingWater) layer1Block).isFlowingDown()) {
                this.getLevel().setBlock(this, 1, get(FLOWING_WATER), true, false);
            }

            int $4 = KELP_AGE.getMax();
            if (down.getId().equals(KELP) && down.getPropertyValue(KELP_AGE) != maxAge - 1) {
                setAge(maxAge - 1);
                this.getLevel().setBlock(down, down, true, true);
            }

            //Placing it by hand gives it a random age value between 0 and 24.
            setAge(ThreadLocalRandom.current().nextInt(maxAge));
            this.getLevel().setBlock(this, this, true, true);
            return true;
        } else {
            return false;
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block $5 = getLevelBlockAtLayer(1);
            if (!(blockLayer1 instanceof BlockFrostedIce) &&
                    (!(blockLayer1 instanceof BlockFlowingWater) || !((BlockFlowingWater) blockLayer1).isSourceOrFlowingDown())) {
                this.getLevel().useBreakOn(this);
                return type;
            }

            Block $6 = down();
            if ((!down.isSolid() && !down.getId().equals(KELP)) || down.getId().equals(MAGMA) || down.getId().equals(ICE) || down.getId().equals(SOUL_SAND)) {
                this.getLevel().useBreakOn(this);
                return type;
            }

            if (blockLayer1 instanceof BlockFlowingWater w && w.isFlowingDown()) {
                this.getLevel().setBlock(this, 1, get(FLOWING_WATER), true, false);
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (ThreadLocalRandom.current().nextInt(100) <= 14) {
                grow();
            }
            return type;
        }
        return super.onUpdate(type);
    }
    /**
     * @deprecated 
     */
    

    public boolean grow() {
        int $7 = getAge();
        int $8 = KELP_AGE.getMax();
        if (age < maxValue) {
            Block $9 = up();
            if (up instanceof BlockFlowingWater w && w.isSourceOrFlowingDown()) {
                Block $10 = new BlockKelp(blockstate.setPropertyValue(PROPERTIES, KELP_AGE.createValue(getAge() + 1)));
                BlockGrowEvent $11 = new BlockGrowEvent(this, grown);
                Server.getInstance().getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    this.setAge(maxValue);
                    this.getLevel().setBlock(this, 0, this, true, true);
                    this.getLevel().setBlock(up, 1, get(FLOWING_WATER), true, false);
                    this.getLevel().setBlock(up, 0, ev.getNewState(), true, true);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onBreak(Item item) {
        Block $12 = down();
        if (down.getId().equals(KELP)) {
            BlockKelp $13 = new BlockKelp(blockstate.setPropertyValue(PROPERTIES, KELP_AGE.createValue(ThreadLocalRandom.current().nextInt(KELP_AGE.getMax()))));
            this.getLevel().setBlock(down, blockKelp, true, true);
        }
        this.getLevel().setBlock(this, get(AIR), true, true);
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        //Bone meal
        if (item.isFertilizer()) {
            int $14 = (int) this.x;
            int $15 = (int) this.z;
            for (int $16 = (int) this.y + 1; y < 255; y++) {
                Block $17 = getLevel().getBlock(x, y, z);
                String $18 = blockAbove.getId();
                if (!Objects.equals(blockIdAbove, KELP)) {
                    if (blockAbove instanceof BlockFlowingWater water) {
                        if (water.isSourceOrFlowingDown()) {
                            BlockKelp $19 = (BlockKelp) getLevel().getBlock(x, y - 1, z);
                            if (highestKelp.grow()) {
                                this.level.addParticle(new BoneMealParticle(this));

                                if (player != null && (player.gamemode & 0x01) == 0) {
                                    item.count--;
                                }

                                return true;
                            }
                        }
                    }
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return true;
    }
}
