package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.Faceable;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author joserobjr
 * @since 2020-09-15
 */


public abstract class BlockCropsStem extends BlockCrops implements Faceable {

    public abstract BlockState getStrippedState();

    //https://minecraft.wiki/w/Melon_Seeds#Breaking
    private static final double[][] dropChances = new double[][]{
            {.8130,.1742,.0124,.0003}, //0
            {.6510,.3004,.0462,.0024}, //1
            {.5120,.3840,.0960,.0080}, //2
            {.3944,.4302,.1564,.0190}, //3
            {.2913,.4444,.2222,.0370}, //4
            {.2160,.4320,.2880,.0640}, //5
            {.1517,.3982,.3484,.1016}, //6
            {.1016,.3484,.3982,.1517}  //7
    };
    
    static {
        for (double[] dropChance : dropChances) {
            double last = dropChance[0];
            for (int i = 1; i < dropChance.length; i++) {
                last += dropChance[i];
                assert last <= 1.0;
                dropChance[i] = last;
            }
        }
    }

    public BlockCropsStem(BlockState blockstate) {
        super(blockstate);
    }

    public abstract String getFruitId();

    public abstract String getSeedsId();

    @Override
    public BlockFace getBlockFace() {
        return getFacing();
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.FACING_DIRECTION, face.getIndex());
    }

    public BlockFace getFacing() {
        return BlockFace.fromIndex(getPropertyValue(CommonBlockProperties.FACING_DIRECTION));
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() != FARMLAND) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
            BlockFace blockFace = getBlockFace();
            if (blockFace.getAxis().isHorizontal() && getSide(blockFace).getId() != getFruitId()) {
                setBlockFace(null);
                getLevel().setBlock(this, this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
            return 0;
        }
        
        if (type != Level.BLOCK_UPDATE_RANDOM) {
            return 0;
        }
        
        if (ThreadLocalRandom.current().nextInt(1, 3) != 1 
                || getLevel().getFullLight(this) < MINIMUM_LIGHT_LEVEL) {
            return Level.BLOCK_UPDATE_RANDOM;
        }
        
        int growth = getGrowth();
        if (growth < CommonBlockProperties.GROWTH.getMax()) {
            BlockCropsStem block = this.clone();
            block.setGrowth(growth + 1);
            BlockGrowEvent ev = new BlockGrowEvent(this, block);
            Server.getInstance().getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                this.getLevel().setBlock(this, ev.getNewState(), true);
            }
            return Level.BLOCK_UPDATE_RANDOM;
        }
        
        growFruit();
        return Level.BLOCK_UPDATE_RANDOM;
    }

    public boolean growFruit() {
        String fruitId = getFruitId();
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            Block b = this.getSide(face);
            if (b.getId().equals(fruitId)) {
                return false;
            }
        }
        
        BlockFace sideFace = BlockFace.Plane.HORIZONTAL.random();
        Block side = this.getSide(sideFace);
        Block d = side.down();
        if (side.isAir() && (d.getId().equals(FARMLAND) || d.getId().equals(GRASS_BLOCK) || d.getId().equals(DIRT))) {
            BlockGrowEvent ev = new BlockGrowEvent(side, Block.get(fruitId));
            Server.getInstance().getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                this.getLevel().setBlock(side, ev.getNewState(), true);
                setBlockFace(sideFace);
                this.getLevel().setBlock(this, this, true);
            }
        }
        return true;
    }

    @Override
    public Item toItem() {
        return Item.get(getSeedsId());
    }

    @Override
    public Item[] getDrops(Item item) {
        double[] dropChance = dropChances[NukkitMath.clamp(getGrowth(), 0, dropChances.length)];
        
        double dice = ThreadLocalRandom.current().nextDouble();
        int count = 0;
        while (dice > dropChance[count]) {
            count++;
        }
        
        if (count == 0) {
            return Item.EMPTY_ARRAY;
        }
        
        return new Item[]{
                Item.get(getSeedsId(), 0, count)
        };
    }

    @Override
    public BlockCropsStem clone() {
        return (BlockCropsStem) super.clone();
    }
}
