package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlowable;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.property.BlockProperties;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Faceable;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Since("1.6.0.0-PNX")
@PowerNukkitOnly
public class BlockSmallDripleaf extends BlockFlowable implements Faceable {
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final BlockProperties PROPERTIES =
            new BlockProperties(CommonBlockProperties.CARDINAL_DIRECTION, CommonBlockProperties.UPPER_BLOCK);

    public BlockSmallDripleaf() {
        super(0);
    }

    @Override
    public String getName() {
        return "Small Dripleaf";
    }

    @Override
    public int getId() {
        return BlockID.SMALL_DRIPLEAF_BLOCK;
    }

    @Since("1.6.0.0-PNX")
    @PowerNukkitOnly
    @NotNull @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(CommonBlockProperties.DIRECTION);
    }

    @Since("1.6.0.0-PNX")
    @PowerNukkitOnly
    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.DIRECTION, face);
    }

    public boolean isUpperBlock() {
        return this.getBooleanValue(CommonBlockProperties.UPPER_BLOCK);
    }

    public void setUpperBlock(boolean isUpperBlock) {
        this.setBooleanValue(CommonBlockProperties.UPPER_BLOCK, isUpperBlock);
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHEARS;
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
            @Nullable Player player) {
        BlockSmallDripleaf dripleaf = new BlockSmallDripleaf();
        BlockSmallDripleaf dripleafTop = new BlockSmallDripleaf();
        dripleafTop.setUpperBlock(true);
        dripleaf.setBlockFace(player.getDirection().getOpposite());
        dripleafTop.setBlockFace(player.getDirection().getOpposite());
        if (canKeepAlive(block)) {
            this.getLevel().setBlock(block, dripleaf, true, true);
            this.getLevel().setBlock(block.getSide(BlockFace.UP), dripleafTop, true, true);
            return true;
        }
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[] {toItem()};
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    public boolean onBreak(@NotNull Item item) {
        this.getLevel().setBlock(this, new BlockAir(), true, true);
        if (item != null && item.isShears()) this.getLevel().dropItem(this, this.toItem());
        if (this.getSide(BlockFace.UP).getId() == BlockID.SMALL_DRIPLEAF_BLOCK) {
            this.getLevel().getBlock(this.getSide(BlockFace.UP)).onBreak(null);
        }
        if (this.getSide(BlockFace.DOWN).getId() == BlockID.SMALL_DRIPLEAF_BLOCK) {
            this.getLevel().getBlock(this.getSide(BlockFace.DOWN)).onBreak(null);
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (!canKeepAlive(this)) {
            this.getLevel().setBlock(this, new BlockAir(), true, true);
            this.getLevel().dropItem(this, this.toItem());
        }
        return super.onUpdate(type);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player) {
        if (item.isFertilizer()) {
            NukkitRandom random = new NukkitRandom();
            int height = random.nextBoundedInt(4) + 2;

            BlockBigDripleaf blockBigDripleafDown = new BlockBigDripleaf();
            BlockBigDripleaf blockBigDripleafHead = new BlockBigDripleaf();
            blockBigDripleafDown.setBlockFace(player.getDirection().getOpposite());
            blockBigDripleafHead.setBlockFace(player.getDirection().getOpposite());
            blockBigDripleafHead.setHead(true);

            Block buttom = this.getBlock();
            while (buttom.getSide(BlockFace.DOWN).getId() == BlockID.SMALL_DRIPLEAF_BLOCK) {
                buttom = buttom.getSide(BlockFace.DOWN);
            }

            for (int i = 0; i < height; i++) {
                this.getLevel().setBlock(buttom.add(0, i, 0), blockBigDripleafDown, true, true);
            }
            this.getLevel().setBlock(buttom.add(0, height, 0), blockBigDripleafHead, true, true);

            this.getLevel().addParticleEffect(this.add(0.5, 0.5, 0.5), ParticleEffect.CROP_GROWTH);
            item.count--;
            return true;
        }
        return false;
    }

    public boolean canKeepAlive(Position pos) {
        Block blockDown = this.getLevel().getBlock(pos.getSide(BlockFace.DOWN));
        Block blockHere = this.getLevel().getBlock(pos, 1);
        Block blockUp = this.getLevel().getBlock(pos.getSide(BlockFace.UP));
        if (this.getLevel().getBlock(blockDown) instanceof BlockClay) {
            return true;
        }
        if (this.getLevel().getBlock(blockDown) instanceof BlockSmallDripleaf
                && !((BlockSmallDripleaf) this.getLevel().getBlock(blockDown)).isUpperBlock()) {
            return true;
        }
        if (blockHere instanceof BlockWater
                && (blockUp instanceof BlockAir || blockUp instanceof BlockSmallDripleaf)
                && (blockDown instanceof BlockGrass
                        || blockDown instanceof BlockDirt
                        || blockDown instanceof BlockDirtWithRoots
                        || blockDown instanceof BlockMoss)) {
            return true;
        }
        return false;
    }
}
