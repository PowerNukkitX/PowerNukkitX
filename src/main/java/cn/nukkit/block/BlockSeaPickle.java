package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.event.block.BlockSpreadEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector2;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.property.CommonBlockProperties.CLUSTER_COUNT;
import static cn.nukkit.block.property.CommonBlockProperties.DEAD_BIT;


public class BlockSeaPickle extends BlockFlowable {
    public static final BlockProperties PROPERTIES = new BlockProperties(SEA_PICKLE, CLUSTER_COUNT, DEAD_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSeaPickle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSeaPickle(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Sea Pickle";
    }

    public boolean isDead() {
        return getPropertyValue(DEAD_BIT);
    }

    public void setDead(boolean dead) {
        setPropertyValue(DEAD_BIT, dead);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block down = down();
            if (!down.isSolid() || down.getId().equals(ICE)) {
                this.getLevel().useBreakOn(this);
                return type;
            }

            Block layer1 = getLevelBlockAtLayer(1);
            if (layer1 instanceof BlockFlowingWater || layer1.getId().equals(FROSTED_ICE)) {
                if (isDead() && (layer1.getId().equals(FROSTED_ICE) || layer1.getPropertyValue(CommonBlockProperties.LIQUID_DEPTH) == 0 || layer1.getPropertyValue(CommonBlockProperties.LIQUID_DEPTH) == 8)) {
                    BlockFadeEvent event = new BlockFadeEvent(this, new BlockSeaPickle().setPropertyValue(DEAD_BIT, !isDead()));
                    if (!event.isCancelled()) {
                        this.getLevel().setBlock(this, event.getNewState(), true, true);
                    }
                    return type;
                }
            } else if (!isDead()) {
                BlockFadeEvent event = new BlockFadeEvent(this, new BlockSeaPickle().setPropertyValue(DEAD_BIT, !isDead()));
                if (!event.isCancelled()) {
                    this.getLevel().setBlock(this, event.getNewState(), true, true);
                }
            }

            return type;
        }

        return 0;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (target.getId().equals(SEA_PICKLE) && target.getPropertyValue(CLUSTER_COUNT) < 3) {
            target.setPropertyValue(CLUSTER_COUNT, target.getPropertyValue(CLUSTER_COUNT) + 1);
            this.getLevel().setBlock(target, target, true, true);
            return true;
        }

        Block down = block.down().getLevelBlockAtLayer(0);
        if (down.isSolid() && !down.getId().equals(ICE)) {
            if (down instanceof BlockSlab || down instanceof BlockStairs || block.getId().equals(BUBBLE_COLUMN)) {
                return false;
            }
            Block layer1 = block.getLevelBlockAtLayer(1);
            if (layer1 instanceof BlockFlowingWater w) {
                if (w.getLiquidDepth() != 0 && w.getLiquidDepth() != 8) {
                    return false;
                }

                if (w.getLiquidDepth() == 8) {
                    this.getLevel().setBlock(block, 1, new BlockFlowingWater(), true, false);
                }
            } else {
                setDead(true);
            }

            this.getLevel().setBlock(block, 0, this, true, true);

            return true;
        }

        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {

        //Bone meal
        if (item.isFertilizer() && down() instanceof BlockCoralBlock && !isDead()) {
            BlockSeaPickle block = (BlockSeaPickle) clone();
            block.setPropertyValue(CLUSTER_COUNT, 3);

            BlockGrowEvent blockGrowEvent = new BlockGrowEvent(this, block);
            Server.getInstance().getPluginManager().callEvent(blockGrowEvent);

            if (blockGrowEvent.isCancelled()) {
                return false;
            }

            this.getLevel().setBlock(this, blockGrowEvent.getNewState(), false, true);
            this.level.addParticle(new BoneMealParticle(this));

            if (player != null && (player.gamemode & 0x01) == 0) {
                item.count--;
            }

            ThreadLocalRandom random = ThreadLocalRandom.current();
            Block[] blocksAround = this.getLevel().getCollisionBlocks(new SimpleAxisAlignedBB(x - 2, y - 2, z - 2, x + 3, y, z + 3));
            for (Block blockNearby : blocksAround) {
                if (blockNearby instanceof BlockCoralBlock) {
                    Block up = blockNearby.up();
                    if (up instanceof BlockFlowingWater w &&
                            (w.getLiquidDepth() == 0 || w.getLiquidDepth() == 8) &&
                            random.nextInt(6) == 0 && new Vector2(up.x, up.z).distance(new Vector2(this.x, this.z)) <= 2) {
                        BlockSpreadEvent blockSpreadEvent = new BlockSpreadEvent(up, this, new BlockSeaPickle().setPropertyValue(CLUSTER_COUNT, random.nextInt(3)));
                        if (!blockSpreadEvent.isCancelled()) {
                            this.getLevel().setBlock(up, 1, new BlockFlowingWater(), true, false);
                            this.getLevel().setBlock(up, blockSpreadEvent.getNewState(), true, true);
                        }
                    }
                }
            }
        }

        return super.onActivate(item, player, blockFace, fx, fy, fz);
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public int getLightLevel() {
        if (isDead()) {
            return 0;
        } else {
            return 6 + getPropertyValue(CLUSTER_COUNT) * 3;
        }
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockSeaPickle());
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{new ItemBlock(new BlockSeaPickle(), 0, getPropertyValue(CLUSTER_COUNT))};
    }
}
