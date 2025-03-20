package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.inventory.HumanInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class MobEquipmentProcessor extends DataPacketProcessor<MobEquipmentPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull MobEquipmentPacket pk) {
        Player player = playerHandle.player;
        if (!player.spawned || !player.isAlive()) {
            return;
        }

        if(pk.hotbarSlot < 0 || pk.hotbarSlot > 8) {
            player.close("§cPacket handling error");
            return;
        }
        if(!pk.item.isNull()) {
            if (pk.item.getEnchantments().length > Enchantment.getEnchantments().length) { // Last Enchant Id
                player.close("§cPacket handling error");
                return;
            }
            if (pk.item.getLore().length > 100) {
                player.close("§cPacket handling error");
                return;
            }
            if (pk.item.getCanPlaceOn().size() > 250) {
                player.close("§cPacket handling error");
                return;
            }
            if (pk.item.getCanDestroy().size() > 250) {
                player.close("§cPacket handling error");
                return;
            }
        }

        Inventory inv = player.getWindowById(pk.windowId);

        if (inv == null) {
            log.debug("Player {} has no open container with window ID {}", player.getName(), pk.windowId);
            return;
        }

        if (inv instanceof HumanInventory inventory && inventory.getHeldItemIndex() == pk.hotbarSlot) {
            return;
        }

        Item item = inv.getItem(pk.hotbarSlot);

        if (!item.equals(pk.item, false, true)) {
            Item fixItem = Item.get(item.getId(), item.getDamage(), item.getCount(), item.getCompoundTag());
            if (fixItem.equals(pk.item, false, true)) {
                inv.setItem(pk.hotbarSlot, fixItem);
            } else {
                log.debug("Tried to equip {} but have {} in target slot", pk.item, fixItem);
                inv.sendContents(player);
            }
        }

        if (inv instanceof HumanInventory inventory) {
            inventory.equipItem(pk.hotbarSlot);
        }

        player.setDataFlag(EntityFlag.USING_ITEM, false);
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.MOB_EQUIPMENT_PACKET;
    }
}
