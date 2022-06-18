package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCaveVines;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Superice666
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class ItemGlowBerries extends ItemEdible {

    public ItemGlowBerries() {
        this(0, 1);
    }

    public ItemGlowBerries(Integer meta) {
        this(meta, 1);
    }

    public ItemGlowBerries(Integer meta, int count) {
        super(GLOW_BERRIES, 0, count, "Glow Berries");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (BlockCaveVines.isValidSupport(block) && face.equals(BlockFace.DOWN)) {
            var tmp = new BlockCaveVines();
            tmp.setPropertyValue(BlockCaveVines.AGE_PROPERTY, ThreadLocalRandom.current().nextInt(26));
            level.setBlock(target.down(), tmp);
            level.addSound(target.down(), Sound.DIG_CAVE_VINES);
            if (player.isAdventure() || player.isSurvival()) {
                --this.count;
                player.getInventory().setItemInHand(this);
            }
            return true;
        }

        return false;
    }
}
