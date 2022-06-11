package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author Gabriel8579
 * @since 2021-06-13
 */
@Since("FUTURE")
public class BlockGlowLichen extends BlockLichen{
    @Override
    public String getName() {
        return "Glow Lichen";
    }

    @Override
    public int getId() {
        return GLOW_LICHEN;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, @Nullable Player player) {

        if (!item.isFertilizer()) {
            return false;
        }

        Map<Block, BlockFace> candidates = getCandidates();

        item.decrement(1);

        if (candidates.isEmpty()) {
            return true;
        }

        Set<Block> keySet = candidates.keySet();
        List<Block> keyList = new ArrayList<>(keySet);

        int rand = RANDOM.nextRange(0, candidates.size() - 1);

        Block random = keyList.get(rand);
        Block newLichen;

        if (random.getId() == BlockID.GLOW_LICHEN) {
            newLichen = random;
        } else {
            newLichen = Block.get(GLOW_LICHEN);
        }

        newLichen.setPropertyValue(MULTI_FACE_DIRECTION_BITS, newLichen.getPropertyValue(MULTI_FACE_DIRECTION_BITS) | (0b000001 << candidates.get(random).getDUSWNEIndex()));

        getLevel().setBlock(random, newLichen, true, true);

        return true;
    }

    @Nonnull
    private Map<Block, BlockFace> getCandidates() {
        Map<Block, BlockFace> candidates = new HashMap<>();
        for (BlockFace side : BlockFace.values()) {
            Block support = this.getSide(side);

            if (isGrowthToSide(side)) {
                BlockFace[] supportSides = side.getEdges().toArray(new BlockFace[0]);

                for (BlockFace supportSide : supportSides) {
                    Block supportNeighbor = support.getSide(supportSide);

                    if (!isSupportNeighborAdded(candidates, supportSide.getOpposite(), supportNeighbor)) {
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
        return candidates;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getLightLevel() {
        return 7;
    }

    private boolean isSupportNeighborAdded(@Nonnull Map<Block, BlockFace> candidates, @Nonnull BlockFace side, @Nonnull Block supportNeighbor) {
        // Air is a valid candidate!
        if (supportNeighbor.getId() == BlockID.AIR) {
            candidates.put(supportNeighbor, side);
        }

        // Other non solid blocks isn't a valid candidates
        return supportNeighbor.isSolid(side);
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
    public BlockColor getColor() {
        return BlockColor.LICHEN_GREEN;
    }
}
