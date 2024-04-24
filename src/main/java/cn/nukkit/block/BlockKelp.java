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
    public static final BlockProperties PROPERTIES = new BlockProperties(KELP, KELP_AGE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockKelp() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockKelp(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Kelp";
    }

    public int getAge() {
        return getPropertyValue(KELP_AGE);
    }

    public void setAge(int age) {
        setPropertyValue(KELP_AGE, age);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = down();
        Block layer1Block = block.getLevelBlockAtLayer(1);
        if ((down.getId().equals(KELP) || down.isSolid()) && !down.getId().equals(MAGMA) && !down.getId().equals(ICE) && !down.getId().equals(SOUL_SAND) &&
                (layer1Block instanceof BlockFlowingWater flowingWater && flowingWater.isSourceOrFlowingDown())
        ) {
            if (((BlockFlowingWater) layer1Block).isFlowingDown()) {
                this.getLevel().setBlock(this, 1, get(FLOWING_WATER), true, false);
            }

            int maxAge = KELP_AGE.getMax();
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
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block blockLayer1 = getLevelBlockAtLayer(1);
            if (!(blockLayer1 instanceof BlockFrostedIce) &&
                    (!(blockLayer1 instanceof BlockFlowingWater) || !((BlockFlowingWater) blockLayer1).isSourceOrFlowingDown())) {
                this.getLevel().useBreakOn(this);
                return type;
            }

            Block down = down();
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

    public boolean grow() {
        int age = getAge();
        int maxValue = KELP_AGE.getMax();
        if (age < maxValue) {
            Block up = up();
            if (up instanceof BlockFlowingWater w && w.isSourceOrFlowingDown()) {
                Block grown = new BlockKelp(blockstate.setPropertyValue(PROPERTIES, KELP_AGE.createValue(getAge() + 1)));
                BlockGrowEvent ev = new BlockGrowEvent(this, grown);
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
    public boolean onBreak(Item item) {
        Block down = down();
        if (down.getId().equals(KELP)) {
            BlockKelp blockKelp = new BlockKelp(blockstate.setPropertyValue(PROPERTIES, KELP_AGE.createValue(ThreadLocalRandom.current().nextInt(KELP_AGE.getMax()))));
            this.getLevel().setBlock(down, blockKelp, true, true);
        }
        this.getLevel().setBlock(this, get(AIR), true, true);
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        //Bone meal
        if (item.isFertilizer()) {
            int x = (int) this.x;
            int z = (int) this.z;
            for (int y = (int) this.y + 1; y < 255; y++) {
                Block blockAbove = getLevel().getBlock(x, y, z);
                String blockIdAbove = blockAbove.getId();
                if (!Objects.equals(blockIdAbove, KELP)) {
                    if (blockAbove instanceof BlockFlowingWater water) {
                        if (water.isSourceOrFlowingDown()) {
                            BlockKelp highestKelp = (BlockKelp) getLevel().getBlock(x, y - 1, z);
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
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }
}
