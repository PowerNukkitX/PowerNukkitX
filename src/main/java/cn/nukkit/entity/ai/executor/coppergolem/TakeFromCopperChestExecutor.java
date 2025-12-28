package cn.nukkit.entity.ai.executor.coppergolem;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.copper.chest.BlockCopperChest;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.executor.EntityControl;
import cn.nukkit.entity.ai.executor.IBehaviorExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.mob.EntityCopperGolem;
import cn.nukkit.inventory.ChestInventory;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.BlockEventPacket;

import java.util.Optional;

/**
 * @author Buddelbubi
 * @since 2025/11/18
 */
public class TakeFromCopperChestExecutor implements EntityControl, IBehaviorExecutor {

    protected int tick = 0;

    private static final int STAY_TICKS = 60;

    private Block chest;

    @Override
    public boolean execute(EntityIntelligent entity) {
        if(tick < STAY_TICKS) {
            tick++;
            return true;
        }
        return false;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        this.tick = 0;
        if(entity instanceof EntityCopperGolem golem) {
            chest = entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_BLOCK_2);
            animateChest(golem, true);
            golem.setEnumEntityProperty(EntityCopperGolem.PROPERTIES[0].getIdentifier(), "take");
            golem.sendData(golem.getViewers().values().toArray(Player[]::new));
        }
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        if(entity instanceof EntityCopperGolem golem) {
            animateChest(golem, false);
            if(chest instanceof BlockCopperChest chest) {
                BlockEntityChest blockEntityChest = chest.getOrCreateBlockEntity();
                ChestInventory chestInventory = (ChestInventory) blockEntityChest.getInventory();
                Optional<Item> optionalItem = chestInventory.getContents().values().stream().filter(item1 -> !item1.isNull()).findFirst();
                if(optionalItem.isPresent()) {
                    Item item = optionalItem.get().clone();
                    if(item.getCount() > 16) {
                        item.setCount(16);
                    }
                    chestInventory.removeItem(item);
                    golem.getInventory().setItemInHand(item, true);
                    golem.sendData(golem.getViewers().values().toArray(Player[]::new));
                    golem.setEnumEntityProperty(EntityCopperGolem.PROPERTIES[0].getIdentifier(), "none");
                } else golem.setEnumEntityProperty(EntityCopperGolem.PROPERTIES[0].getIdentifier(), "take_fail");
                golem.sendData(golem.getViewers().values().toArray(Player[]::new));
                var copperchests = entity.getMemoryStorage().get(CoreMemoryTypes.COPPER_CHESTS);
                copperchests.addLast(blockEntityChest);
                if(copperchests.size() >= 10) {
                    entity.getMemoryStorage().put(CoreMemoryTypes.FORCE_WANDERING, 7*20);
                    copperchests.clear();
                }
            }
        }
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        onStop(entity);
    }

    public void animateChest(EntityCopperGolem entity, boolean open) {
        if(chest instanceof BlockCopperChest) {
            BlockEventPacket packet = new BlockEventPacket();
            packet.x = (int) chest.getX();
            packet.y = (int) chest.getY();
            packet.z = (int) chest.getZ();
            packet.type = 1;
            packet.value = open ? 2 : 0;
            Server.broadcastPacket(entity.getViewers().values(), packet);
        }
    }
}
