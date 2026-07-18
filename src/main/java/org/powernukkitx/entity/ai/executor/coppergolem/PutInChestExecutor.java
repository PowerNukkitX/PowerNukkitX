package org.powernukkitx.entity.ai.executor.coppergolem;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockChest;
import org.powernukkitx.blockentity.BlockEntityChest;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.executor.EntityControl;
import org.powernukkitx.entity.ai.executor.IBehaviorExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.mob.EntityCopperGolem;
import org.powernukkitx.inventory.ChestInventory;
import org.powernukkitx.item.Item;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.packet.BlockEventPacket;

/**
 * @author Buddelbubi
 * @since 2025/11/18
 */
public class PutInChestExecutor implements EntityControl, IBehaviorExecutor {

    protected int tick = 0;

    private static final int STAY_TICKS = 60;

    private Block chest;

    @Override
    public boolean execute(EntityIntelligent entity) {
        if (tick < STAY_TICKS) {
            tick++;
            return true;
        }
        return false;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        this.tick = 0;
        if (entity instanceof EntityCopperGolem golem) {
            chest = entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_BLOCK);
            animateChest(golem, true);
            golem.setEnumEntityProperty(EntityCopperGolem.PROPERTIES[0].getIdentifier(), "put");
            golem.sendData(golem.getViewers().values().toArray(Player[]::new));
        }
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        if (entity instanceof EntityCopperGolem golem) {
            animateChest(golem, false);
            if (chest instanceof BlockChest chest) {
                BlockEntityChest blockEntityChest = chest.getOrCreateBlockEntity();
                ChestInventory chestInventory = (ChestInventory) blockEntityChest.getInventory();
                Item item = golem.getInventory().getItemInHand();
                Item single = item.clone();
                single.setCount(1);
                var chests = entity.getMemoryStorage().get(CoreMemoryTypes.CHESTS);
                chests.addLast(blockEntityChest);
                if (chests.size() >= 10) {
                    entity.getMemoryStorage().put(CoreMemoryTypes.FORCE_WANDERING, 7 * 20);
                    chests.clear();
                }
                if (chestInventory.isEmpty() || chestInventory.contains(single)) {
                    golem.getInventory().clear(0, true);
                    golem.setEnumEntityProperty(EntityCopperGolem.PROPERTIES[0].getIdentifier(), "none");
                    chestInventory.addItem(item);
                    chests.clear();
                    entity.getMemoryStorage().get(CoreMemoryTypes.COPPER_CHESTS).clear();
                } else golem.setEnumEntityProperty(EntityCopperGolem.PROPERTIES[0].getIdentifier(), "put_fail");
                golem.sendData(golem.getViewers().values().toArray(Player[]::new));
            }
        }
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        onStop(entity);
    }

    public void animateChest(EntityCopperGolem entity, boolean open) {
        if (chest instanceof BlockChest) {
            final BlockEventPacket packet = new BlockEventPacket();
            packet.setBlockPosition(Vector3i.from(chest.getX(), chest.getY(), chest.getZ()));
            packet.setEventType(1);
            packet.setEventValue(open ? 2 : 0);
            Server.broadcastPacket(entity.getViewers().values(), packet);
        }
    }
}
