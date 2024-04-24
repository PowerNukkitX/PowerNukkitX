package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.level.StructureGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.ObjectMangroveTree;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.property.CommonBlockProperties.HANGING;
import static cn.nukkit.block.property.CommonBlockProperties.PROPAGULE_STAGE;

public class BlockMangrovePropagule extends BlockFlowable implements BlockFlowerPot.FlowerPotBlock {

    public static final BlockProperties PROPERTIES = new BlockProperties(MANGROVE_PROPAGULE, HANGING, PROPAGULE_STAGE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangrovePropagule() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangrovePropagule(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Mangrove Propaugle";
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        //todo: 实现红树树苗放置逻辑
        if (BlockFlower.isSupportValid(down())) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }

        return false;
    }

    public boolean isHanging() {
        return getPropertyValue(HANGING);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isFertilizer()) { // BoneMeal
            if (player != null && !player.isCreative()) {
                item.count--;
            }

            this.level.addParticle(new BoneMealParticle(this));
            if (ThreadLocalRandom.current().nextFloat() >= 0.45) {
                return true;
            }

            this.grow();

            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!BlockFlower.isSupportValid(down())) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) { //Growth
            this.grow();
        }
        return Level.BLOCK_UPDATE_NORMAL;
    }

    protected void grow() {
        BlockManager chunkManager = new BlockManager(this.level);
        Vector3 vector3 = new Vector3(this.x, this.y - 1, this.z);
        var objectMangroveTree = new ObjectMangroveTree();
        objectMangroveTree.generate(chunkManager, new NukkitRandom(), this);
        StructureGrowEvent ev = new StructureGrowEvent(this, chunkManager.getBlocks());
        this.level.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }
        chunkManager.applySubChunkUpdate(ev.getBlockList());
        this.level.setBlock(this, Block.get(BlockID.AIR));
        if (this.level.getBlock(vector3).getId().equals(BlockID.DIRT_WITH_ROOTS)) {
            this.level.setBlock(vector3, Block.get(BlockID.DIRT));
        }
        for (Block block : ev.getBlockList()) {
            if (block.isAir()) continue;
            this.level.setBlock(block, block);
        }
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }
}
