package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.block.BlockTrappedChest;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.network.protocol.BlockEventPacket;
import cn.nukkit.utils.LevelException;
import cn.nukkit.utils.RedstoneComponent;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ChestInventory extends ContainerInventory {

    protected DoubleChestInventory doubleInventory;

    public ChestInventory(BlockEntityChest chest) {
        super(chest, InventoryType.CHEST);
    }

    @Override
    public BlockEntityChest getHolder() {
        return (BlockEntityChest) this.holder;
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
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
        } catch (LevelException ignored) {}
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
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
        super.onClose(who);

        try {
            if (this.getHolder().getBlock() instanceof BlockTrappedChest trappedChest) {
                RedstoneUpdateEvent event = new RedstoneUpdateEvent(trappedChest);
                this.getHolder().level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    RedstoneComponent.updateAllAroundRedstone(this.getHolder());
                }
            }
        } catch (LevelException ignored) {}
    }

    public void setDoubleInventory(DoubleChestInventory doubleInventory) {
        this.doubleInventory = doubleInventory;
    }

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
}
