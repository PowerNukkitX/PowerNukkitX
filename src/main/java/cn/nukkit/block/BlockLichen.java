package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;


public abstract class BlockLichen extends BlockTransparent {
    public static final NukkitRandom RANDOM = new NukkitRandom();

    public BlockLichen(BlockState blockState) {
        super(blockState);
    }

    public BlockFace[] getGrowthSides() {
        Stream<BlockFace> returns = Arrays.stream(BlockFace.values()).filter(this::isGrowthToSide);
        return returns.toArray(BlockFace[]::new);
    }

    public void witherAtSide(BlockFace side) {
        if (isGrowthToSide(side)) {
            setPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS, getPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS) ^ (0b000001 << side.getDUSWNEIndex()));
            getLevel().setBlock(this, this, true, true);
        }
    }

    public boolean isGrowthToSide(@NotNull BlockFace side) {
        return ((getPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS) >> side.getDUSWNEIndex()) & 0x1) > 0;
    }

    public void growToSide(BlockFace side) {
        if (!isGrowthToSide(side)) {
            setPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS, getPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS) | (0b000001 << side.getDUSWNEIndex()));
            getLevel().setBlock(this, this, true, true);
        }
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {

        if (!target.isSolid() && target instanceof BlockLichen) {
            return false;
        }

        int currentMeta = 0;
        if (block instanceof BlockLichen) {
            currentMeta = block.getPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS);
        }

        setPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS, currentMeta | (0b000001 << face.getOpposite().getDUSWNEIndex()));

        if (getPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS) == currentMeta) {
            BlockFace[] sides = BlockFace.values();
            Stream<BlockFace> faceStream = Arrays.stream(sides).filter(side ->
                    block.getSide(side).isSolid(side) && !isGrowthToSide(side)
            );
            Optional<BlockFace> optionalFace = faceStream.findFirst();
            if (optionalFace.isPresent()) {
                growToSide(optionalFace.get());
                return true;
            }

            return false;
        }

        getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        for (BlockFace side : BlockFace.values()) {
            final Block support = this.getSide(side);
            if (isGrowthToSide(side) && support != null && !support.isSolid()) {
                this.witherAtSide(side);
            }
        }
        return super.onUpdate(type);
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public double getResistance() {
        return 1;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
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

    @Override
    public boolean canHarvest(Item item) {
        return item.isAxe() || item.isShears();
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this.getProperties().getDefaultState().toBlock());
    }
}
