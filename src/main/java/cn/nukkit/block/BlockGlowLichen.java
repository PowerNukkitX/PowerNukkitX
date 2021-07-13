package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author Gabriel8579
 * @since 2021-06-13
 */
@Since("FUTURE")
public class BlockGlowLichen extends BlockTransparent {

    public static final IntBlockProperty MULTI_FACE_DIRECTION_BITS = new IntBlockProperty("multi_face_direction_bits", false, 63, 0, 6);

    @PowerNukkitOnly
    @Since("FUTURE")
    public static final BlockProperties PROPERTIES = new BlockProperties(MULTI_FACE_DIRECTION_BITS);

    public BlockGlowLichen() {
        super();
    }

    @Override
    public String getName() {
        return "Glow Lichen";
    }

    @Override
    public int getId() {
        return GLOW_LICHEN;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockFace[] getGrowthSides() {
        Stream<BlockFace>returns = Arrays.stream(BlockFace.values()).filter(this::isGrowthToSide);
        return returns.toArray(BlockFace[]::new);
    }

    public boolean isGrowthToSide(@Nonnull BlockFace side) {
        return ((getPropertyValue(MULTI_FACE_DIRECTION_BITS) >> side.getIndex()) & 0x1) > 0;
    }

    public void growToSide(BlockFace side) {
        if (!isGrowthToSide(side)) {
            setPropertyValue(MULTI_FACE_DIRECTION_BITS, getPropertyValue(MULTI_FACE_DIRECTION_BITS) | (0b0001 << side.getIndex()));
            getLevel().setBlock(this, this, true, true);
        }
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {

        if (!target.isSolid() && target.getId() != BlockID.GLOW_LICHEN) {
            return false;
        }

        int currentMeta = 0;
        if (block.getId() == GLOW_LICHEN) {
            currentMeta = block.getPropertyValue(MULTI_FACE_DIRECTION_BITS);
        }

        setPropertyValue(MULTI_FACE_DIRECTION_BITS, currentMeta | (0b0001 << face.getOpposite().getIndex()));

        if (getPropertyValue(MULTI_FACE_DIRECTION_BITS) == currentMeta) {
            BlockFace[] sides = BlockFace.values();
            Stream<BlockFace> faceStream = Arrays.stream(sides).filter(side ->
                    block.getSide(side).isSolid() && !isGrowthToSide(side)
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
    public boolean onActivate(@Nonnull Item item, @Nullable Player player) {

        if (!item.isFertilizer()) {
            return false;
        }

        Map<Block, BlockFace> candidates = new HashMap<>();

        for (int i = 0; i < 5; i++) {
            BlockFace side = BlockFace.fromIndex(i);
            Block support = this.getSide(side);

            if (isGrowthToSide(side)) {
                BlockFace[] supportSides = side.getEdges().toArray(new BlockFace[0]);

                for (BlockFace supportSide : supportSides) {
                    Block supportNeighbor = support.getSide(supportSide);

                    // Air is a valid candidate!
                    if (supportNeighbor.getId() == BlockID.AIR) {
                        candidates.put(supportNeighbor, supportSide.getOpposite());
                    }

                    // Other non solid blocks isn't a valid candidates
                    if (!supportNeighbor.isSolid()) {
                        continue;
                    }

                    Block supportNeighborOppositeSide = supportNeighbor.getSide(side.getOpposite());
                    if (shouldAddSupportNeighborOppositeSide(side, supportNeighborOppositeSide)) {
                        candidates.put(supportNeighborOppositeSide, side);
                    }

                }

            } else {
                if (support.isSolid()) {
                    candidates.put(this, side);
                }
            }
        }

        item.decrement(1);

        if (candidates.isEmpty()) {
            return true;
        }

        Set<Block> keySet = candidates.keySet();
        List<Block> keyList = new ArrayList<>(keySet);

        int rand = new NukkitRandom().nextRange(0, candidates.size() - 1);

        Block random = keyList.get(rand);
        Block newLichen;

        if (random.getId() == BlockID.GLOW_LICHEN) {
            newLichen = random;
        } else {
            newLichen = Block.get(GLOW_LICHEN);
        }

        newLichen.setPropertyValue(MULTI_FACE_DIRECTION_BITS, newLichen.getPropertyValue(MULTI_FACE_DIRECTION_BITS) | (0b0001 << candidates.get(random).getIndex()));

        getLevel().setBlock(random, newLichen, true, true);

        return true;
    }

    private boolean shouldAddSupportNeighborOppositeSide(@Nonnull BlockFace side, @Nonnull Block supportNeighborOppositeSide) {
        if (supportNeighborOppositeSide.getId() == BlockID.AIR || supportNeighborOppositeSide.getId() == BlockID.GLOW_LICHEN) {
            return supportNeighborOppositeSide.getId() != BlockID.GLOW_LICHEN ||
                    (!((BlockGlowLichen) supportNeighborOppositeSide).isGrowthToSide(side) &&
                            supportNeighborOppositeSide.getSide(side).getId() != BlockID.AIR);
        }
        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
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
    public int getLightLevel() {
        return 7;
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

    @Override
    public BlockColor getColor() {
        return BlockColor.LICHEN_GREEN;
    }

    @PowerNukkitDifference(info = "Prevents players from getting invalid items by limiting the return to the maximum damage defined in getMaxItemDamage()", since = "1.4.0.0-PN")
    @Override
    public Item toItem() {
        return new ItemBlock(this);
    }
}
