package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockWater extends BlockLiquid {
    public static final BlockProperties PROPERTIES = new BlockProperties(TALLGRASS, CommonBlockProperties.LIQUID_DEPTH);

    public BlockWater() {
        this(0);
    }

    public BlockWater(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return FLOWING_WATER;
    }

    @Override
    public String getName() {
        return "Water";
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

        int newId = newBlock.getId();
        if (newId == FLOWING_WATER || newId == STILL_WATER) {
            return;
        }

        Block up = up(1, 0);
        for (BlockFace diagonalFace : BlockFace.Plane.HORIZONTAL) {
            Block diagonal = up.getSide(diagonalFace);
            if (diagonal.getId() == BlockID.REEDS) {
                diagonal.onUpdate(Level.BLOCK_UPDATE_SCHEDULED);
            }
        }
    }

    @Override
    public BlockLiquid getBlock(int meta) {
        return (BlockLiquid) Block.get(BlockID.FLOWING_WATER, meta);
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
