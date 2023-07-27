package cn.nukkit.network.process.processor;

import cn.nukkit.player.Player;
import cn.nukkit.player.PlayerHandle;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class MobEquipmentProcessor extends DataPacketProcessor<MobEquipmentPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull MobEquipmentPacket pk) {
        Player player = playerHandle.player;
        if (!player.spawned || !player.isAlive()) {
            return;
        }

        Inventory inv = player.getWindowById(pk.windowId);

        if (inv == null) {
            log.debug("Player {} has no open container with window ID {}", player.getName(), pk.windowId);
            return;
        }

        Item item = inv.getItem(pk.hotbarSlot);

        if (!item.equals(pk.item)) {
            log.debug("Tried to equip {} but have {} in target slot", pk.item, item);
            inv.sendContents(player);
            return;
        }

        if (inv instanceof PlayerInventory inventory) {
            inventory.equipItem(pk.hotbarSlot);
        }

        player.setDataFlag(Player.DATA_FLAGS, Player.DATA_FLAG_ACTION, false);
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.MOB_EQUIPMENT_PACKET);
    }
}
