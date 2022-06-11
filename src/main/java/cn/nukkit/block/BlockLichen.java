package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class BlockLichen extends BlockTransparent {

    public static final IntBlockProperty MULTI_FACE_DIRECTION_BITS = new IntBlockProperty("multi_face_direction_bits", false, 63, 0, 6);
    public static final NukkitRandom RANDOM = new NukkitRandom();

    @PowerNukkitOnly
    @Since("FUTURE")
    public static final BlockProperties PROPERTIES = new BlockProperties(MULTI_FACE_DIRECTION_BITS);

    @Since("FUTURE")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockFace[] getGrowthSides() {
        Stream<BlockFace> returns = Arrays.stream(BlockFace.values()).filter(this::isGrowthToSide);
        return returns.toArray(BlockFace[]::new);
    }

    public void witherAtSide(BlockFace side) {
        if (isGrowthToSide(side)) {
            setPropertyValue(MULTI_FACE_DIRECTION_BITS, getPropertyValue(MULTI_FACE_DIRECTION_BITS) ^ (0b000001 << side.getDUSWNEIndex()));
            getLevel().setBlock(this, this, true, true);
        }
    }

    public boolean isGrowthToSide(@Nonnull BlockFace side) {
        return ((getPropertyValue(MULTI_FACE_DIRECTION_BITS) >> side.getDUSWNEIndex()) & 0x1) > 0;
    }

    public void growToSide(BlockFace side) {
        if (!isGrowthToSide(side)) {
            setPropertyValue(MULTI_FACE_DIRECTION_BITS, getPropertyValue(MULTI_FACE_DIRECTION_BITS) | (0b000001 << side.getDUSWNEIndex()));
            getLevel().setBlock(this, this, true, true);
        }
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {

        if (!target.isSolid() && target instanceof BlockLichen) {
            return false;
        }

        int currentMeta = 0;
        if (block instanceof BlockLichen) {
            currentMeta = block.getPropertyValue(MULTI_FACE_DIRECTION_BITS);
        }

        setPropertyValue(MULTI_FACE_DIRECTION_BITS, currentMeta | (0b000001 << face.getOpposite().getDUSWNEIndex()));

        if (getPropertyValue(MULTI_FACE_DIRECTION_BITS) == currentMeta) {
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

    @PowerNukkitOnly
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

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
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

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @PowerNukkitDifference(info = "Prevents players from getting invalid items by limiting the return to the maximum damage defined in getMaxItemDamage()", since = "1.4.0.0-PN")
    @Override
    public Item toItem() {
        return new ItemBlock(this);
    }
}
