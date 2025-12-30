package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.types.LevelSoundEvent;
import cn.nukkit.utils.random.NukkitRandom;

/**
 * @author Leonidius20
 * @since 20.08.18
 */
public class ItemChorusFruit extends ItemFood {

    public ItemChorusFruit() {
        this(0, 1);
    }

    public ItemChorusFruit(Integer meta) {
        this(meta, 1);
    }

    public ItemChorusFruit(Integer meta, int count) {
        super(CHORUS_FRUIT, meta, count, "Chorus Fruit");
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return player.isItemCoolDownEnd(this.getIdentifier());
    }

    @Override
    public void afterUse(Player player) {
        player.setItemCoolDown(20, this.getIdentifier());//ban 20tick for the item
    }

    @Override
    public boolean onEaten(Player player) {
        int minX = player.getFloorX() - 8;
        int minY = player.getFloorY() - 8;
        int minZ = player.getFloorZ() - 8;
        int maxX = minX + 16;
        int maxY = minY + 16;
        int maxZ = minZ + 16;

        Level level = player.getLevel();
        if (level == null) return false;
        if (player.isInsideOfWater()) return false;

        NukkitRandom random = new NukkitRandom();
        for (int attempts = 0; attempts < 128; attempts++) {
            int x = random.nextInt(minX, maxX);
            int y = random.nextInt(minY, maxY);
            int z = random.nextInt(minZ, maxZ);

            if (y < 0) continue;

            while (y >= 0 && !level.getBlock(new Vector3(x, y + 1, z)).isSolid()) {
                y--;
            }
            y++; // Back up to non solid

            Block blockUp = level.getBlock(new Vector3(x, y + 1, z));
            Block blockUp2 = level.getBlock(new Vector3(x, y + 2, z));

            if (blockUp.isSolid() || blockUp instanceof BlockLiquid ||
                    blockUp2.isSolid() || blockUp2 instanceof BlockLiquid) {
                continue;
            }

            // Sounds are broadcast at both source and destination
            level.addLevelSoundEvent(player, LevelSoundEvent.TELEPORT);
            player.teleport(new Vector3(x + 0.5, y + 1, z + 0.5), PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT);
            level.addLevelSoundEvent(player, LevelSoundEvent.TELEPORT);

            break;
        }

        return true;
    }
}
