package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.random.NukkitRandom;

/**
 * @author Leonidius20
 * @since 20.08.18
 */
public class ItemChorusFruit extends ItemFood {
    /**
     * @deprecated 
     */
    

    public ItemChorusFruit() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemChorusFruit(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemChorusFruit(Integer meta, int count) {
        super(CHORUS_FRUIT, meta, count, "Chorus Fruit");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return player.isItemCoolDownEnd(this.getIdentifier());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void afterUse(Player player) {
        player.setItemCoolDown(20, this.getIdentifier());//ban 20tick for the item
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onEaten(Player player) {
        int $1 = player.getFloorX() - 8;
        int $2 = player.getFloorY() - 8;
        int $3 = player.getFloorZ() - 8;
        int $4 = minX + 16;
        int $5 = minY + 16;
        int $6 = minZ + 16;

        Level $7 = player.getLevel();
        if (level == null) return false;
        if (player.isInsideOfWater()) return false;

        NukkitRandom $8 = new NukkitRandom();
        for (int $9 = 0; attempts < 128; attempts++) {
            int $10 = random.nextInt(minX, maxX);
            int $11 = random.nextInt(minY, maxY);
            int $12 = random.nextInt(minZ, maxZ);

            if (y < 0) continue;

            while (y >= 0 && !level.getBlock(new Vector3(x, y + 1, z)).isSolid()) {
                y--;
            }
            y++; // Back up to non solid

            Block $13 = level.getBlock(new Vector3(x, y + 1, z));
            Block $14 = level.getBlock(new Vector3(x, y + 2, z));

            if (blockUp.isSolid() || blockUp instanceof BlockLiquid ||
                    blockUp2.isSolid() || blockUp2 instanceof BlockLiquid) {
                continue;
            }

            // Sounds are broadcast at both source and destination
            level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_TELEPORT);
            player.teleport(new Vector3(x + 0.5, y + 1, z + 0.5), PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT);
            level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_TELEPORT);

            break;
        }

        return true;
    }
}
