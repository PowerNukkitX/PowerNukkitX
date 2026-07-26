package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.event.level.StructureGrowEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.ObjectBigMushroom;
import org.powernukkitx.level.particle.BoneMealParticle;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public abstract class BlockMushroom extends BlockFlowable implements BlockFlowerPot.FlowerPotBlock, Natural {

    public BlockMushroom(BlockState blockState) {
        super(blockState);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!canStay()) {
                getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (canStay()) {
            getLevel().setBlock(block, this, true, true);
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
        if (item.isFertilizer()) {
            if (player != null && (player.gamemode & 0x01) == 0) {
                item.count--;
            }

            if (ThreadLocalRandom.current().nextFloat() < 0.4) {
                this.grow();
            }

            this.level.addParticle(new BoneMealParticle(this));
            return true;
        }
        return false;
    }

    public boolean grow() {
        this.level.setBlock(this, Block.get(BlockID.AIR), true, false);

        ObjectBigMushroom generator = new ObjectBigMushroom(getType());

        BlockManager chunkManager = new BlockManager(this.level);
        if (generator.generate(chunkManager, RandomSourceProvider.create(), this)) {
            StructureGrowEvent ev = new StructureGrowEvent(this, chunkManager.getBlocks());
            this.level.getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }
            for(Block block : ev.getBlockList()) {
                this.level.setBlock(new Vector3(block.getFloorX(), block.getFloorY(), block.getFloorZ()), block);
            }
            return true;
        } else {
            this.level.setBlock(this, this, true, false);
            return false;
        }
    }

    public boolean canStay() {
        Block block = this.down();
        return block.getId().equals(MYCELIUM) || block.getId().equals(PODZOL) || block instanceof BlockNylium || (!block.isTransparent() && this.level.getFullLight(this) < 13);
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    protected abstract ObjectBigMushroom.MushroomType getType();

    @Override
    public boolean isFertilizable() {
        return true;
    }

    @Override
    public int getSnowloggingLevel() {
        return 1;
    }
}
