package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityEnderChest;
import cn.nukkit.blockentity.BlockEntityNameable;
import cn.nukkit.entity.IHuman;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.bedrock.packet.BlockEventPacket;
import org.cloudburstmc.protocol.bedrock.packet.ContainerClosePacket;
import org.cloudburstmc.protocol.bedrock.packet.ContainerOpenPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Slf4j
public class HumanEnderChestInventory extends BaseInventory implements BlockEntityInventoryNameable {
    @Nullable
    private BlockEntityEnderChest enderChest;

    public HumanEnderChestInventory(IHuman human) {
        super(human, ContainerType.CONTAINER, 27);
    }

    @Override
    public void init() {
        Map<Integer, ContainerEnumName> map = super.slotTypeMap();
        for (int i = 0; i < getSize(); i++) {
            map.put(i, ContainerEnumName.LEVEL_ENTITY_CONTAINER);
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
        final ContainerOpenPacket containerOpenPacket = new ContainerOpenPacket();
        containerOpenPacket.setContainerID((byte) who.getWindowId(this));
        containerOpenPacket.setContainerType(this.getType());
        containerOpenPacket.setPosition(Vector3i.from(enderChest.getX(), enderChest.getY(), enderChest.getZ()));
        super.onOpen(who);
        who.dataPacket(containerOpenPacket);
        this.sendContents(who);

        final BlockEventPacket blockEventPacket = new BlockEventPacket();
        blockEventPacket.setBlockPosition(Vector3i.from(this.getHolder().getX(), this.getHolder().getY(), this.getHolder().getZ()));
        blockEventPacket.setEventType(1);
        blockEventPacket.setEventValue(2);

        Level level = this.getHolder().getLevel();
        if (level != null) {
            level.addSound(this.getHolder().getVector3().add(0.5, 0.5, 0.5), Sound.RANDOM_ENDERCHESTOPEN);
            level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, blockEventPacket);
        }
    }

    @Override
    public Map<Integer, ContainerEnumName> slotTypeMap() {
        Map<Integer, ContainerEnumName> map = super.slotTypeMap();
        map.put(SpecialWindowId.CONTAINER_ID_REGISTRY.getId(), ContainerEnumName.ANVIL_INPUT_CONTAINER);
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

        final ContainerClosePacket containerClosePacket = new ContainerClosePacket();
        containerClosePacket.setContainerID((byte) who.getWindowId(this));
        containerClosePacket.setServerInitiatedClose(who.getClosingWindowId() != containerClosePacket.getContainerID());
        containerClosePacket.setContainerType(this.getType());
        who.dataPacket(containerClosePacket);

        final BlockEventPacket blockEventPacket = new BlockEventPacket();
        blockEventPacket.setBlockPosition(Vector3i.from(enderChest.getX(), enderChest.getY(), enderChest.getZ()));
        blockEventPacket.setEventType(1);
        blockEventPacket.setEventValue(0);

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