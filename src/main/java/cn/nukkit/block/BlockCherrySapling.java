package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.event.level.StructureGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.ListChunkManager;
import cn.nukkit.level.generator.object.tree.ObjectCherryTree;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.player.Player;
import java.util.concurrent.ThreadLocalRandom;
import org.jetbrains.annotations.NotNull;

@Since("1.20.0-r2")
@PowerNukkitXOnly
public class BlockCherrySapling extends BlockFlowable implements BlockFlowerPot.FlowerPotBlock {

    public static final BlockProperties PROPERTIES = new BlockProperties(BlockSapling.AGED);

    @PowerNukkitOnly
    public BlockCherrySapling() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockCherrySapling(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHERRY_SAPLING;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Cherry Sapling";
    }

    public boolean isAged() {
        return getPropertyValue(BlockSapling.AGED);
    }

    public void setAged(boolean aged) {
        setPropertyValue(BlockSapling.AGED, aged);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (isSupportInvalid()) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) { // Growth
            if (getLevel().getFullLight(add(0, 1, 0)) >= BlockCrops.MINIMUM_LIGHT_LEVEL) {
                if (isAged()) {
                    this.grow();
                } else {
                    setAged(true);
                    this.getLevel().setBlock(this, this, true);
                    return Level.BLOCK_UPDATE_RANDOM;
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }
        return Level.BLOCK_UPDATE_NORMAL;
    }

    private void grow() {
        ListChunkManager chunkManager = new ListChunkManager(this.getLevel());
        Vector3 vector3 = new Vector3(this.x(), this.y() - 1, this.z());
        var objectCherryTree = new ObjectCherryTree();
        objectCherryTree.generate(chunkManager, new NukkitRandom(), this);
        StructureGrowEvent event = new StructureGrowEvent(this, chunkManager.getBlocks());
        event.call();
        if (event.isCancelled()) {
            return;
        }
        if (this.getLevel().getBlock(vector3).getId() == BlockID.DIRT_WITH_ROOTS) {
            this.getLevel().setBlock(vector3, Block.get(BlockID.DIRT));
        }
        for (Block block : event.getBlockList()) {
            if (block.getId() == BlockID.AIR) continue;
            this.getLevel().setBlock(block, block);
        }
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
        if (isSupportInvalid()) {
            return false;
        }

        if (this.getLevelBlock() instanceof BlockLiquid || this.getLevelBlockAtLayer(1) instanceof BlockLiquid) {
            return false;
        }

        this.getLevel().setBlock(this, this, true, true);
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player) {
        if (item.isFertilizer()) { // BoneMeal and so on
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

    private boolean isSupportInvalid() {
        int downId = down().getId();
        return !(downId == DIRT || downId == GRASS || downId == SAND || downId == GRAVEL || downId == PODZOL);
    }

    public boolean isAge() {
        return this.getPropertyValue(BlockSapling.AGED);
    }

    public void setAge(boolean age) {
        this.setBooleanValue(BlockSapling.AGED, age);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockCherrySapling());
    }
}
