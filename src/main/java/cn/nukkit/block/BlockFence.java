package cn.nukkit.block;

import cn.nukkit.block.definition.BlockDefinition;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;

import static cn.nukkit.math.VectorMath.calculateFace;

/**
 * @author xtypr
 * @since 2015/12/7
 */
public abstract class BlockFence extends BlockTransparent implements BlockConnectable {
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(2)
            .resistance(3)
            .toolType(ItemTool.TYPE_AXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .burnChance(5)
            .burnAbility(20)
            .build();

    public BlockFence(BlockState blockState) {
        this(blockState, DEFINITION);
    }

    public BlockFence(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        boolean north = this.canConnect(this.north());
        boolean south = this.canConnect(this.south());
        boolean west = this.canConnect(this.west());
        boolean east = this.canConnect(this.east());
        double n = north ? 0 : 0.375;
        double s = south ? 1 : 0.625;
        double w = west ? 0 : 0.375;
        double e = east ? 1 : 0.625;
        return new SimpleAxisAlignedBB(
                this.x + w,
                this.y,
                this.z + n,
                this.x + e,
                this.y + 1.5,
                this.z + s
        );
    }

    @Override
    public boolean canConnect(Block block) {
        if (block instanceof BlockFence) {
            if (block.getId().equals(NETHER_BRICK_FENCE) || this.getId().equals(NETHER_BRICK_FENCE)) {
                return block.getId().equals(this.getId());
            }
            return true;
        }
        if (block instanceof BlockTrapdoor trapdoor) {
            return trapdoor.isOpen() && trapdoor.getBlockFace() == calculateFace(this, trapdoor);
        }
        return block instanceof BlockFenceGate || block.isSolid() && !block.isTransparent();
    }

}
