package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityEnderChest;
import cn.nukkit.blockentity.BlockEntityNameable;
import cn.nukkit.entity.IHuman;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.network.protocol.BlockEventPacket;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Slf4j
public class HumanEnderChestInventory extends BaseInventory implements BlockEntityInventoryNameable {
    @Nullable
    private BlockEntityEnderChest enderChest;

    public HumanEnderChestInventory(IHuman human) {
        super(human, InventoryType.CONTAINER, 27);
    }

    @Override
    public void init() {
        Map<Integer, ContainerSlotType> map = super.slotTypeMap();
        for (int i = 0; i < getSize(); i++) {
            map.put(i, ContainerSlotType.LEVEL_ENTITY);
        }
    }

    @Override
    public IHuman getHolder() {
        return (IHuman) this.holder;
    }

    public void setBlockEntityEnderChest(@NotNull Player player, BlockEntityEnderChest blockEntityEnderChest) {
        if (blockEntityEnderChest == null) {
            enderChest = null;
            player.setEnderChestOpen(false);
        } else {
            enderChest = blockEntityEnderChest;
            player.setEnderChestOpen(true);
        }
    }

    @Override
    public void onOpen(Player who) {
        if (who != this.getHolder()) {
            return;
        }
        if (enderChest == null) {
            return;
        }
        ContainerOpenPacket containerOpenPacket = new ContainerOpenPacket();
        containerOpenPacket.windowId = who.getWindowId(this);
        containerOpenPacket.type = this.getType().getNetworkType();
        containerOpenPacket.x = (int) enderChest.getX();
        containerOpenPacket.y = (int) enderChest.getY();
        containerOpenPacket.z = (int) enderChest.getZ();
        super.onOpen(who);
        who.dataPacket(containerOpenPacket);
        this.sendContents(who);

        BlockEventPacket blockEventPacket = new BlockEventPacket();
        blockEventPacket.x = (int) enderChest.getX();
        blockEventPacket.y = (int) enderChest.getY();
        blockEventPacket.z = (int) enderChest.getZ();
        blockEventPacket.type = 1;
        blockEventPacket.value = 2;

        Level level = this.getHolder().getLevel();
        if (level != null) {
            level.addSound(this.getHolder().getVector3().add(0.5, 0.5, 0.5), Sound.RANDOM_ENDERCHESTOPEN);
            level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, blockEventPacket);
        }
    }

    @Override
    public Map<Integer, ContainerSlotType> slotTypeMap() {
        Map<Integer, ContainerSlotType> map = super.slotTypeMap();
        map.put(SpecialWindowId.CONTAINER_ID_REGISTRY.getId(), ContainerSlotType.ANVIL_INPUT);
        return map;
    }

    @Override
    public void onClose(Player who) {
        if (who != this.getHolder()) {
            return;
        }
        if (enderChest == null) {
            return;
        }

        ContainerClosePacket containerClosePacket = new ContainerClosePacket();
        containerClosePacket.windowId = who.getWindowId(this);
        containerClosePacket.wasServerInitiated = who.getClosingWindowId() != containerClosePacket.windowId;
        containerClosePacket.type = getType();
        who.dataPacket(containerClosePacket);

        BlockEventPacket blockEventPacket = new BlockEventPacket();
        blockEventPacket.x = (int) enderChest.getX();
        blockEventPacket.y = (int) enderChest.getY();
        blockEventPacket.z = (int) enderChest.getZ();
        blockEventPacket.type = 1;
        blockEventPacket.value = 0;

        Level level = this.getHolder().getLevel();
        if (level != null) {
            level.addSound(this.getHolder().getVector3().add(0.5, 0.5, 0.5), Sound.RANDOM_ENDERCHESTCLOSED);
            level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, blockEventPacket);
        }
        setBlockEntityEnderChest(who, null);
        super.onClose(who);
    }

    @Override
    @Nullable
    public BlockEntityNameable getBlockEntityInventoryHolder() {
        return enderChest;
    }

    @Override
    public void setInventoryTitle(String name) {
        if (enderChest != null) {
            enderChest.setName(name);
        }
    }

    @Override
    public String getInventoryTitle() {
        if (enderChest != null) {
            return enderChest.getName();
        } else return "Unknown";
    }
}
