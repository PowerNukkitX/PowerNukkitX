package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.block.BlockTrappedChest;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.blockentity.BlockEntityNameable;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.network.protocol.BlockEventPacket;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import cn.nukkit.utils.LevelException;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ChestInventory extends ContainerInventory implements BlockEntityInventoryNameable {
    @Nullable
    protected DoubleChestInventory doubleInventory;

    public ChestInventory(BlockEntityChest chest) {
        super(chest, InventoryType.CONTAINER, 27);
    }

    @Override
    public void init() {
        Map<Integer, ContainerSlotType> map = super.slotTypeMap();
        for (int i = 0; i < getSize(); i++) {
            map.put(i, ContainerSlotType.LEVEL_ENTITY);
        }
    }


    @Override
    public BlockEntityChest getHolder() {
        return (BlockEntityChest) this.holder;
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);

        if (this.getViewers().size() == 1) {
            BlockEventPacket pk = new BlockEventPacket();
            pk.x = (int) this.getHolder().getX();
            pk.y = (int) this.getHolder().getY();
            pk.z = (int) this.getHolder().getZ();
            pk.case1 = 1;
            pk.case2 = 2;

            Level level = this.getHolder().getLevel();
            if (level != null) {
                level.addSound(this.getHolder().add(0.5, 0.5, 0.5), Sound.RANDOM_CHESTOPEN);
                level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, pk);
            }
        }
        try {
            if (this.getHolder().getBlock() instanceof BlockTrappedChest trappedChest) {
                RedstoneUpdateEvent event = new RedstoneUpdateEvent(trappedChest);
                this.getHolder().level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    RedstoneComponent.updateAllAroundRedstone(this.getHolder());
                }
            }
        } catch (LevelException ignored) {
        }
    }

    @Override
    public void onClose(Player who) {
        if (this.getViewers().size() == 1) {
            BlockEventPacket pk = new BlockEventPacket();
            pk.x = (int) this.getHolder().getX();
            pk.y = (int) this.getHolder().getY();
            pk.z = (int) this.getHolder().getZ();
            pk.case1 = 1;
            pk.case2 = 0;

            Level level = this.getHolder().getLevel();
            if (level != null) {
                level.addSound(this.getHolder().add(0.5, 0.5, 0.5), Sound.RANDOM_CHESTCLOSED);
                level.addChunkPacket((int) this.getHolder().getX() >> 4, (int) this.getHolder().getZ() >> 4, pk);
            }
        }

        try {
            if (this.getHolder().getBlock() instanceof BlockTrappedChest trappedChest) {
                RedstoneUpdateEvent event = new RedstoneUpdateEvent(trappedChest);
                this.getHolder().level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    RedstoneComponent.updateAllAroundRedstone(this.getHolder());
                }
            }
        } catch (LevelException ignored) {
        }
        super.onClose(who);
    }

    public void setDoubleInventory(@NotNull DoubleChestInventory doubleInventory) {
        this.doubleInventory = doubleInventory;
    }

    @Nullable
    public DoubleChestInventory getDoubleInventory() {
        return doubleInventory;
    }

    @Override
    public void sendSlot(int index, Player... players) {
        if (this.doubleInventory != null) {
            this.doubleInventory.sendSlot(this, index, players);
        } else {
            super.sendSlot(index, players);
        }
    }

    @Override
    public boolean canCauseVibration() {
        return true;
    }

    @Override
    public BlockEntityNameable getBlockEntityInventoryHolder() {
        return getHolder();
    }
}
