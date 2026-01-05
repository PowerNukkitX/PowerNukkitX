package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.enums.OxidizationLevel;
import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.particle.ScrapeParticle;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author joserobjr
 * @since 2021-06-14
 */
public interface Oxidizable {

    @NotNull
    Location getLocation();

    default int onUpdate(int type) {
        if (type != Level.BLOCK_UPDATE_RANDOM) {
            return 0;
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (random.nextFloat() >= 64F / 1125F) {
            return 0;
        }

        int oxiLvl = getOxidizationLevel().ordinal();
        if (oxiLvl == OxidizationLevel.OXIDIZED.ordinal()) {
            return 0;
        }

        // Just to make sure we don't accidentally degrade a waxed block.
        if ((this instanceof Waxable) && ((Waxable) this).isWaxed()) {
            return 0;
        }

        Block block = this instanceof Block ? (Block) this : getLocation().getLevelBlock();
        Location mutableLocation = block.getLocation();

        int odds = 0;
        int cons = 0;

        for (int x = -4; x <= 4; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -4; z <= 4; z++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }
                    mutableLocation.setComponents(block.x + x, block.y + y, block.z + z);
                    if (block.distanceManhattan(mutableLocation) > 4) {
                        continue;
                    }
                    Block relative = mutableLocation.getLevelBlock();
                    if (!(relative instanceof Oxidizable)) {
                        continue;
                    }
                    int relOxiLvl = ((Oxidizable) relative).getOxidizationLevel().ordinal();
                    if (relOxiLvl < oxiLvl) {
                        return type;
                    }

                    if (relOxiLvl > oxiLvl) {
                        cons++;
                    } else {
                        odds++;
                    }
                }
            }
        }

        float chance = (float) (cons + 1) / (float) (cons + odds + 1);
        float multiplier = oxiLvl == 0 ? 0.75F : 1.0F;
        chance = chance * chance * multiplier;
        if (random.nextFloat() < chance) {
            Block nextBlock = getBlockWithOxidizationLevel(OxidizationLevel.values()[oxiLvl + 1]);
            BlockFadeEvent event = new BlockFadeEvent(block, nextBlock);
            block.getLevel().getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                block.getLevel().setBlock(block, event.getNewState());
            }
        }
        return type;
    }

    default boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (!item.isAxe()) {
            return false;
        }

        OxidizationLevel oxidizationLevel = getOxidizationLevel();
        if (OxidizationLevel.UNAFFECTED.equals(oxidizationLevel)) {
            return false;
        }

        oxidizationLevel = OxidizationLevel.values()[oxidizationLevel.ordinal() - 1];
        if (!setOxidizationLevel(oxidizationLevel)) {
            return false;
        }

        Position location = this instanceof Block ? (Position) this : getLocation();
        if (player == null || !player.isCreative()) {
            item.useOn(this instanceof Block ? (Block) this : location.getLevelBlock());
        }
        location.getValidLevel().addParticle(new ScrapeParticle(location));
        return true;
    }

    @NotNull OxidizationLevel getOxidizationLevel();

    boolean setOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel);

    Block getBlockWithOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel);
}
