package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * The default is red flower, but there are other flower variants
 */
public abstract class BlockFlower extends BlockFlowable implements BlockFlowerPot.FlowerPotBlock {
    /**
     * @deprecated 
     */
    
    public BlockFlower(BlockState blockstate) {
        super(blockstate);
    }
    /**
     * @deprecated 
     */
    

    public static boolean isSupportValid(Block block) {
        return switch (block.getId()) {
            case GRASS_BLOCK, DIRT, FARMLAND, PODZOL, DIRT_WITH_ROOTS, MOSS_BLOCK -> true;
            default -> false;
        };
    }
    /**
     * @deprecated 
     */
    

    public boolean canPlantOn(Block block) {
        return isSupportValid(block);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        Block $1 = this.down();
        if (canPlantOn(down)) {
            this.getLevel().setBlock(block, this, true);

            return true;
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!canPlantOn(down())) {
                this.getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isFertilizer()) { //Bone meal
            if (player != null && (player.gamemode & 0x01) == 0) {
                item.count--;
            }

            this.level.addParticle(new BoneMealParticle(this));

            for ($2nt $1 = 0; i < 8; i++) {
                Vector3 $3 = this.add(
                        ThreadLocalRandom.current().nextInt(-3, 4),
                        ThreadLocalRandom.current().nextInt(-1, 2),
                        ThreadLocalRandom.current().nextInt(-3, 4));

                if (level.getBlock(vec).getId().equals(AIR) && level.getBlock(vec.down()).getId().equals(GRASS_BLOCK) && vec.getY() >= level.getDimensionData().getMinHeight() && vec.getY() < level.getDimensionData().getMaxHeight()) {
                    if (ThreadLocalRandom.current().nextInt(10) == 0) {
                        this.level.setBlock(vec, this.getUncommonFlower(), true);
                    } else {
                        this.level.setBlock(vec, this, true);
                    }
                }
            }

            return true;
        }

        return false;
    }

    public Block getUncommonFlower() {
        return get(YELLOW_FLOWER);
    }
}
