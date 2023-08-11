package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlowable;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.property.BlockProperties;
import cn.nukkit.block.property.BooleanBlockProperty;
import cn.nukkit.block.property.IntBlockProperty;
import cn.nukkit.event.level.StructureGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.ListChunkManager;
import cn.nukkit.level.generator.object.tree.ObjectMangroveTree;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.player.Player;
import java.util.concurrent.ThreadLocalRandom;
import org.jetbrains.annotations.NotNull;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockMangrovePropagule extends BlockFlowable implements BlockFlowerPot.FlowerPotBlock {

    public static final BooleanBlockProperty HANGING = new BooleanBlockProperty("hanging", false);
    public static final IntBlockProperty PROPAGULE_STAGE = new IntBlockProperty("propagule_stage", false, 4, 0);

    public static final BlockProperties PROPERTIES = new BlockProperties(HANGING, PROPAGULE_STAGE);

    @Override
    public String getName() {
        return "Mangrove Propaugle";
    }

    @Override
    public int getId() {
        return MANGROVE_PROPAGULE;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public boolean place(
            @NotNull Item item,
            @NotNull Block block,
            @NotNull Block target,
            @NotNull BlockFace face,
            double fx,
            double fy,
            double fz,
            Player player) {
        // todo: 实现红树树苗放置逻辑
        if (BlockFlower.isSupportValid(down())) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }

        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player) {
        if (item.isFertilizer()) { // BoneMeal
            if (player != null && !player.isCreative()) {
                item.count--;
            }

            this.getLevel().addParticle(new BoneMealParticle(this));
            if (ThreadLocalRandom.current().nextFloat() >= 0.45) {
                return true;
            }

            this.grow();

            return true;
        }
        return false;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will break on block update if the supporting block is invalid")
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!BlockFlower.isSupportValid(down())) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) { // Growth
            this.grow();
        }
        return Level.BLOCK_UPDATE_NORMAL;
    }

    public void grow() {
        ListChunkManager chunkManager = new ListChunkManager(this.getLevel());
        Vector3 vector3 = new Vector3(this.x(), this.y() - 1, this.z());
        var objectMangroveTree = new ObjectMangroveTree();
        objectMangroveTree.generate(chunkManager, new NukkitRandom(), this);
        StructureGrowEvent ev = new StructureGrowEvent(this, chunkManager.getBlocks());
        ev.call();
        if (ev.isCancelled()) {
            return;
        }
        this.getLevel().setBlock(this, Block.get(BlockID.AIR));
        if (this.getLevel().getBlock(vector3).getId() == BlockID.DIRT_WITH_ROOTS) {
            this.getLevel().setBlock(vector3, Block.get(BlockID.DIRT));
        }
        for (Block block : ev.getBlockList()) {
            if (block.getId() == BlockID.AIR) continue;
            this.getLevel().setBlock(block, block);
        }
    }
    ;
}
