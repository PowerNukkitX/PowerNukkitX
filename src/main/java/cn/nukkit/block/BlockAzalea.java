package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockproperty.value.WoodType;
import cn.nukkit.event.level.StructureGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.ListChunkManager;
import cn.nukkit.level.generator.object.BasicGenerator;
import cn.nukkit.level.generator.object.tree.ObjectAzaleaTree;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author LoboMetalurgico
 * @since 13/06/2021
 */

@PowerNukkitOnly
@Since("FUTURE")
public class BlockAzalea extends BlockFlowable implements BlockFlowerPot.FlowerPotBlock {


    public static final BlockProperties PROPERTIES = CommonBlockProperties.EMPTY_PROPERTIES;

    @PowerNukkitOnly
    public BlockAzalea() {
        this(0);
    }


    @PowerNukkitOnly
    public BlockAzalea(int meta) {
        super(meta);
    }


    @PowerNukkitOnly
    @Since("FUTURE")
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Azalea";
    }

    @Override
    public int getId() {
        return AZALEA;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_NONE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }


    @Override
    public boolean onActivate(@NotNull Item item, Player player) {
        if (item.isFertilizer()) { // BoneMeal
            if (player != null && !player.isCreative()) {
                item.count--;
            }

            this.level.addParticle(new BoneMealParticle(this));
            if (ThreadLocalRandom.current().nextInt(4) == 0) {
                this.grow();
                return true;
            }
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        double chance = ThreadLocalRandom.current().nextDouble(1);
        boolean aged = chance > 0.8;
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!BlockFlower.isSupportValid(down())) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) { //Growth
            if (ThreadLocalRandom.current().nextInt(1, 8) == 1 && getLevel().getFullLight(add(0, 1, 0)) >= BlockCrops.MINIMUM_LIGHT_LEVEL) {
                if (aged) {
                    this.grow();
                } else {
                    this.getLevel().setBlock(this, this, true);
                    return Level.BLOCK_UPDATE_RANDOM;
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }
        return Level.BLOCK_UPDATE_NORMAL;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (BlockFlower.isSupportValid(down())) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }

        return false;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{toItem()};
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isSameType(Vector3 pos, WoodType type) {
        Block block = this.level.getBlock(pos);
        return block.getId() == this.getId() && ((BlockSapling) block).getWoodType() == type;
    }

    private void grow() {
        BasicGenerator generator = null;
        Vector3 vector3 = new Vector3();

        generator = new ObjectAzaleaTree();
        vector3 = this.add(0, 0, 0);

        ListChunkManager chunkManager = new ListChunkManager(this.level);
        boolean success = generator.generate(chunkManager, new NukkitRandom(), vector3);
        StructureGrowEvent ev = new StructureGrowEvent(this, chunkManager.getBlocks());
        this.level.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled() || !success) {
            return;
        }

        for (Block block : ev.getBlockList()) {
            this.level.setBlock(block, block);
        }
        this.level.setBlock(this, Block.get(LOG));
    }
}
