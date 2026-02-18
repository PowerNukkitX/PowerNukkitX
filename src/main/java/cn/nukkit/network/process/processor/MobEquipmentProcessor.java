package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.inventory.HumanInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.MobEquipmentPacket;
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

        if (pk.getHotbarSlot() < 0 || pk.getHotbarSlot() > 8) {
            player.close("§cPacket handling error");
            return;
        }

        Inventory inv = player.getWindowById(pk.getContainerId());

        if (inv == null) {
            log.debug("Player {} has no open container with window ID {}", player.getName(), pk.getContainerId());
            return;
        }

        if (inv instanceof HumanInventory inventory && inventory.getHeldItemIndex() == pk.getHotbarSlot()) {
            return;
        }

        if (inv instanceof HumanInventory inventory) {
            inventory.equipItem(pk.getHotbarSlot());
        }

        player.setDataFlag(EntityFlag.USING_ITEM, false);
    }

    @Override
    public Class<MobEquipmentPacket> getPacketClass() {
        return MobEquipmentPacket.class;
    }
}
