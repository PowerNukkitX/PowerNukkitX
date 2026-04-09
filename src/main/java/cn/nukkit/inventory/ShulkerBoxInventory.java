package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityShulkerBox;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.tags.BlockTags;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.bedrock.packet.BlockEventPacket;

import java.util.Map;

/**
 * @author PetteriM1
 */
public class ShulkerBoxInventory extends ContainerInventory {
    public ShulkerBoxInventory(BlockEntityShulkerBox box) {
        super(box, ContainerType.CONTAINER, 27);
    }

    @Override
    public BlockEntityShulkerBox getHolder() {
        return (BlockEntityShulkerBox) this.holder;
    }

    @Override
    public void init() {
        Map<Integer, ContainerEnumName> map = super.slotTypeMap();
        for (int i = 0; i < getSize(); i++) {
            map.put(i, ContainerEnumName.SHULKER_BOX_CONTAINER);
        }
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);

        if (this.getVisibleViewersCount() == 1) {
            final BlockEventPacket pk = new BlockEventPacket();
            pk.setBlockPosition(Vector3i.from(this.getHolder().getX(), this.getHolder().getY(), this.getHolder().getZ()));
            pk.setEventType(1);
            pk.setEventValue(2);

            Level level = this.getHolder().getLevel();
            if (level != null) {
                level.addSound(this.getHolder().add(0.5, 0.5, 0.5), Sound.RANDOM_SHULKERBOXOPEN);
                level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, pk);
            }
        }
    }

    @Override
    public void onClose(Player who) {
        if (this.getVisibleViewersCount() == 1) {
            final BlockEventPacket pk = new BlockEventPacket();
            pk.setBlockPosition(Vector3i.from(this.getHolder().getX(), this.getHolder().getY(), this.getHolder().getZ()));
            pk.setEventType(1);
            pk.setEventValue(0);

            Level level = this.getHolder().getLevel();
            if (level != null) {
                level.addSound(this.getHolder().add(0.5, 0.5, 0.5), Sound.RANDOM_SHULKERBOXCLOSED);
                level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, pk);
            }
        }

        super.onClose(who);
    }

    @Override
    public boolean canAddItem(Item item) {
        if (item.isBlock() && item.getBlockUnsafe().hasTag(BlockTags.PNX_SHULKERBOX)) {
            // Do not allow nested shulker boxes.
            return false;
        }
        return super.canAddItem(item);
    }

    @Override
    public boolean canCauseVibration() {
        return true;
    }
}
