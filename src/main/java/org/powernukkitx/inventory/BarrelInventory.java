package org.powernukkitx.inventory;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockBarrel;
import org.powernukkitx.blockentity.BlockEntityBarrel;
import org.powernukkitx.blockentity.BlockEntityNameable;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Sound;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;

import java.util.Map;


public class BarrelInventory extends ContainerInventory implements BlockEntityInventoryNameable {
    public BarrelInventory(BlockEntityBarrel barrel) {
        super(barrel, ContainerType.CONTAINER, 27);
    }

    @Override
    public void init() {
        Map<Integer, ContainerEnumName> map = super.slotTypeMap();
        for (int i = 0; i < getSize(); i++) {
            map.put(i, ContainerEnumName.BARREL_CONTAINER);
        }
    }

    @Override
    public BlockEntityBarrel getHolder() {
        return (BlockEntityBarrel) this.holder;
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);

        if (this.getVisibleViewersCount() == 1) {
            BlockEntityBarrel barrel = this.getHolder();
            Level level = barrel.getLevel();
            if (level != null) {
                Block block = barrel.getBlock();
                if (block instanceof BlockBarrel blockBarrel) {
                    if (!blockBarrel.isOpen()) {
                        blockBarrel.setOpen(true);
                        level.setBlock(blockBarrel, blockBarrel, true, true);
                        level.addSound(blockBarrel, Sound.BLOCK_BARREL_OPEN);
                    }
                }
            }
        }
    }

    @Override
    public void onClose(Player who) {
        if (this.getVisibleViewersCount() == 1) {
            BlockEntityBarrel barrel = this.getHolder();
            Level level = barrel.getLevel();
            if (level != null) {
                Block block = barrel.getBlock();
                if (block instanceof BlockBarrel blockBarrel) {
                    if (blockBarrel.isOpen()) {
                        blockBarrel.setOpen(false);
                        level.setBlock(blockBarrel, blockBarrel, true, true);
                        level.addSound(blockBarrel, Sound.BLOCK_BARREL_CLOSE);
                    }
                }
            }
        }

        super.onClose(who);
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
