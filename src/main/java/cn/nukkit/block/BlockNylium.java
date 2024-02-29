package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.ObjectNyliumVegetation;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;


public abstract class BlockNylium extends BlockSolid {
    public BlockNylium(BlockState blockState) {
        super(blockState);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM && !up().isTransparent()) {
            level.setBlock(this, Block.get(NETHERRACK), false);
            return type;
        }
        return 0;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        Block up = up();
        if (item.isNull() || !item.isFertilizer() || !up.isAir()) {
            return false;
        }

        if (player != null && !player.isCreative()) {
            item.count--;
        }

        grow();

        level.addParticle(new BoneMealParticle(up));

        return true;
    }

    public boolean grow() {
        BlockManager blockManager = new BlockManager(this.level);
        ObjectNyliumVegetation.growVegetation(blockManager, this, new NukkitRandom());
        blockManager.applySubChunkUpdate();
        return true;
    }

    @Override
    public double getResistance() {
        return 0.4;
    }

    @Override
    public double getHardness() {
        return 0.4;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{Item.get(NETHERRACK)};
        }
        return Item.EMPTY_ARRAY;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
