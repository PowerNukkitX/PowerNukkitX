package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.particle.BoneMealParticle;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * The default is red flower, but there are other flower variants
 */
public abstract class BlockFlower extends BlockFlowable implements BlockFlowerPot.FlowerPotBlock, Natural, Supportable, Pollinable {
    public BlockFlower(BlockState blockstate) {
        super(blockstate);
    }

    public boolean canPlantOn(Block block) {
        return isSupportDirt(block);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = this.down();
        if (canPlantOn(down)) {
            this.getLevel().setBlock(block, this, true);

            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!canPlantOn(getLevelBlock().down())) {
                this.getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isFertilizer()) { //Bone meal
            if (player != null && (player.gamemode & 0x01) == 0) {
                item.count--;
            }

            this.level.addParticle(new BoneMealParticle(this));

            for (int i = 0; i < 8; i++) {
                Vector3 vec = this.add(
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

    @Override
    public int getSnowloggingLevel() {
        return 1;
    }

    public Block getUncommonFlower() {
        return get(DANDELION);
    }
}
