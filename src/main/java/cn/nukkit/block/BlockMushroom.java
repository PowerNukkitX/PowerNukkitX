package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.impl.BlockFlowerPot;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.event.level.StructureGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.ListChunkManager;
import cn.nukkit.level.generator.object.mushroom.BigMushroom;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.player.Player;
import java.util.concurrent.ThreadLocalRandom;
import org.jetbrains.annotations.NotNull;

public abstract class BlockMushroom extends BlockFlowable implements BlockFlowerPot.FlowerPotBlock {

    public BlockMushroom() {
        this(0);
    }

    public BlockMushroom(int meta) {
        super(0);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.EMPTY_PROPERTIES;
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
    public boolean place(
            @NotNull Item item,
            @NotNull Block block,
            @NotNull Block target,
            @NotNull BlockFace face,
            double fx,
            double fy,
            double fz,
            Player player) {
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
    public boolean onActivate(@NotNull Item item, Player player) {
        if (item.isFertilizer()) {
            if (player != null && (player.gamemode & 0x01) == 0) {
                item.count--;
            }

            if (ThreadLocalRandom.current().nextFloat() < 0.4) {
                this.grow();
            }

            this.getLevel().addParticle(new BoneMealParticle(this));
            return true;
        }
        return false;
    }

    public boolean grow() {
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, false);

        BigMushroom generator = new BigMushroom(getType());

        ListChunkManager chunkManager = new ListChunkManager(this.getLevel());
        if (generator.generate(chunkManager, new NukkitRandom(), this)) {
            StructureGrowEvent event = new StructureGrowEvent(this, chunkManager.getBlocks());
            event.call();
            if (event.isCancelled()) {
                return false;
            }
            for (Block block : event.getBlockList()) {
                this.getLevel()
                        .setBlockAt(
                                block.getFloorX(),
                                block.getFloorY(),
                                block.getFloorZ(),
                                block.getId(),
                                block.getDamage());
            }
            return true;
        } else {
            this.getLevel().setBlock(this, this, true, false);
            return false;
        }
    }

    public boolean canStay() {
        Block block = this.down();
        return block.getId() == MYCELIUM
                || block.getId() == PODZOL
                || block instanceof BlockNylium
                || (!block.isTransparent() && this.getLevel().getFullLight(this) < 13);
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
    @PowerNukkitOnly
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    protected abstract int getType();
}
