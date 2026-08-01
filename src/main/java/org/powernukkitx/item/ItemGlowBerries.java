package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockCaveVines;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.definition.ItemDefinition;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.BlockFace;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Superice666
 */
public class ItemGlowBerries extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .canBeActivated(true)
            .nutrition(2)
            .saturation(0.4f)
            .build();

    public ItemGlowBerries() {
        this(0, 1);
    }

    public ItemGlowBerries(Integer meta) {
        this(meta, 1);
    }

    public ItemGlowBerries(Integer meta, int count) {
        super(GLOW_BERRIES, 0, count, "Glow Berries", DEFINITION);
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (BlockCaveVines.isValidSupport(block) && face.equals(BlockFace.DOWN)) {
            var tmp = new BlockCaveVines();
            tmp.setPropertyValue(CommonBlockProperties.GROWING_PLANT_AGE, ThreadLocalRandom.current().nextInt(26));
            level.setBlock(target.down(), tmp);
            level.addSound(target.down(), Sound.DIG_CAVE_VINES);
            if (player.isAdventure() || player.isSurvival()) {
                --this.count;
                player.getInventory().setItemInMainHand(this);
            }
            return true;
        }

        return false;
    }
}
