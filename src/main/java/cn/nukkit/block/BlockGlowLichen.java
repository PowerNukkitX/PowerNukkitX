package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @author Gabriel8579
 * @since 2021-06-13
 */

public class BlockGlowLichen extends BlockLichen {
    public static final BlockProperties PROPERTIES = new BlockProperties(GLOW_LICHEN, CommonBlockProperties.MULTI_FACE_DIRECTION_BITS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGlowLichen() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockGlowLichen(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Glow Lichen";
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
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

        int rand = RANDOM.nextInt(0, candidates.size() - 1);

        Block random = keyList.get(rand);
        Block newLichen;

        if (random.getId().equals(BlockID.GLOW_LICHEN)) {
            newLichen = random;
        } else {
            newLichen = Block.get(GLOW_LICHEN);
        }

        newLichen.setPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS, newLichen.getPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS) | (0b000001 << candidates.get(random).getDUSWNEIndex()));

        getLevel().setBlock(random, newLichen, true, true);

        return true;
    }

    private @NotNull Map<Block, BlockFace> getCandidates() {
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

    private boolean isSupportNeighborAdded(@NotNull Map<Block, BlockFace> candidates, @NotNull BlockFace side, @NotNull Block supportNeighbor) {
        // Air is a valid candidate!
        if (supportNeighbor.getId().equals(BlockID.AIR)) {
            candidates.put(supportNeighbor, side);
        }

        // Other non-solid blocks isn't a valid candidates
        return supportNeighbor.isSolid(side);
    }

    private boolean shouldAddSupportNeighborOppositeSide(@NotNull BlockFace side, @NotNull Block supportNeighborOppositeSide) {
        if (supportNeighborOppositeSide.getId().equals(BlockID.AIR) || supportNeighborOppositeSide.getId().equals(BlockID.GLOW_LICHEN)) {
            return !supportNeighborOppositeSide.getId().equals(BlockID.GLOW_LICHEN) ||
                    (!((BlockGlowLichen) supportNeighborOppositeSide).isGrowthToSide(side) &&
                            !supportNeighborOppositeSide.getSide(side).getId().equals(BlockID.AIR));
        }
        return false;
    }

    @Override
    public boolean isFertilizable() {
        return true;
    }
}
