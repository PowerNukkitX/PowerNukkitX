package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.CommonPropertyMap;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockSmallDripleafBlock extends BlockFlowable implements Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(SMALL_DRIPLEAF_BLOCK, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSmallDripleafBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSmallDripleafBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Small Dripleaf";
    }

    @Override
    public BlockFace getBlockFace() {
        return CommonPropertyMap.CARDINAL_BLOCKFACE.get(getPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION));
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(face));
    }

    public boolean isUpperBlock() {
        return this.getPropertyValue(CommonBlockProperties.UPPER_BLOCK_BIT);
    }

    public void setUpperBlock(boolean isUpperBlock) {
        this.setPropertyValue(CommonBlockProperties.UPPER_BLOCK_BIT, isUpperBlock);
    }

    @Override
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHEARS;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        BlockSmallDripleafBlock dripleaf = new BlockSmallDripleafBlock();
        BlockSmallDripleafBlock dripleafTop = new BlockSmallDripleafBlock();
        dripleafTop.setUpperBlock(true);
        BlockFace direction = player != null ? player.getDirection().getOpposite() : BlockFace.SOUTH;
        dripleaf.setBlockFace(direction);
        dripleafTop.setBlockFace(direction);
        if (canKeepAlive(block)) {
            this.level.setBlock(block, dripleaf, true, true);
            this.level.setBlock(block.getSide(BlockFace.UP), dripleafTop, true, true);
            return true;
        }
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{toItem()};
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    public boolean onBreak(@NotNull Item item) {
        this.level.setBlock(this, new BlockAir(), true, true);
        if (item.isShears())
            this.level.dropItem(this, this.toItem());
        if (this.getSide(BlockFace.UP).getId().equals(BlockID.SMALL_DRIPLEAF_BLOCK)) {
            this.level.getBlock(this.getSide(BlockFace.UP)).onBreak(null);
        }
        if (this.getSide(BlockFace.DOWN).getId().equals(BlockID.SMALL_DRIPLEAF_BLOCK)) {
            this.level.getBlock(this.getSide(BlockFace.DOWN)).onBreak(null);
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (!canKeepAlive(this)) {
            this.level.setBlock(this, new BlockAir(), true, true);
            this.level.dropItem(this, this.toItem());
        }
        return super.onUpdate(type);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isFertilizer()) {
            NukkitRandom random = new NukkitRandom();
            int height = random.nextInt(4) + 2;

            BlockBigDripleaf blockBigDripleafDown = new BlockBigDripleaf();
            BlockBigDripleaf blockBigDripleafHead = new BlockBigDripleaf();
            BlockFace direction = player != null ? player.getDirection().getOpposite() : BlockFace.SOUTH;
            blockBigDripleafDown.setBlockFace(direction);
            blockBigDripleafHead.setBlockFace(direction);
            blockBigDripleafHead.setHead(true);

            Block buttom = this.clone();
            while (buttom.getSide(BlockFace.DOWN).getId().equals(BlockID.SMALL_DRIPLEAF_BLOCK)) {
                buttom = buttom.getSide(BlockFace.DOWN);
            }

            for (int i = 0; i < height; i++) {
                this.level.setBlock(buttom.add(0, i, 0), blockBigDripleafDown, true, true);
            }
            this.level.setBlock(buttom.add(0, height, 0), blockBigDripleafHead, true, true);

            this.level.addParticleEffect(this.add(0.5, 0.5, 0.5), ParticleEffect.CROP_GROWTH);
            item.count--;
            return true;
        }
        return false;
    }

    public boolean canKeepAlive(Position pos) {
        Block blockDown = this.level.getBlock(pos.getSide(BlockFace.DOWN));
        Block blockHere = this.level.getBlock(pos, 1);
        Block blockUp = this.level.getBlock(pos.getSide(BlockFace.UP));
        if (this.level.getBlock(blockDown) instanceof BlockClay) {
            return true;
        }
        if (this.level.getBlock(blockDown) instanceof BlockSmallDripleafBlock && !((BlockSmallDripleafBlock) this.level.getBlock(blockDown)).isUpperBlock()) {
            return true;
        }
        if (blockHere instanceof BlockFlowingWater && (blockUp instanceof BlockAir || blockUp instanceof BlockSmallDripleafBlock) && (blockDown instanceof BlockGrassBlock || blockDown instanceof BlockDirt || blockDown instanceof BlockDirtWithRoots || blockDown instanceof BlockMossBlock)) {
            return true;
        }
        return false;
    }
}