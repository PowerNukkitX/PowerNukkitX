package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockFlowingWater extends BlockLiquid {
    public static final BlockProperties PROPERTIES = new BlockProperties(FLOWING_WATER, CommonBlockProperties.LIQUID_DEPTH);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockFlowingWater() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockFlowingWater(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Flowing Water";
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        boolean ret = this.getLevel().setBlock(this, this, true, false);
        this.getLevel().scheduleUpdate(this, this.tickRate());
        return ret;
    }

    @Override
    public void afterRemoval(Block newBlock, boolean update) {
        if (!update) {
            return;
        }

        String newId = newBlock.getId();
        if (newId.equals(FLOWING_WATER) || newId.equals(WATER)) {
            return;
        }

        Block up = up(1, 0);
        for (BlockFace diagonalFace : BlockFace.Plane.HORIZONTAL) {
            Block diagonal = up.getSide(diagonalFace);
            if (diagonal.getId().equals(BlockID.REEDS)) {
                diagonal.onUpdate(Level.BLOCK_UPDATE_SCHEDULED);
            }
        }
    }

    @Override
    public BlockLiquid getLiquidWithNewDepth(int depth) {
        return new BlockFlowingWater(this.blockstate.setPropertyValue(PROPERTIES, CommonBlockProperties.LIQUID_DEPTH.createValue(depth)));
    }

    @Override
    public void onEntityCollide(Entity entity) {
        super.onEntityCollide(entity);

        if (entity.fireTicks > 0) {
            entity.extinguish();
        }
    }

    @Override
    public int tickRate() {
        return 5;
    }

    @Override
    public boolean usesWaterLogging() {
        return true;
    }

    @Override
    public double getPassableBlockFrictionFactor() {
        return 0.5;
    }
}