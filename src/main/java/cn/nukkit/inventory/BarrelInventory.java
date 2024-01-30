package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBarrel;
import cn.nukkit.blockentity.BlockEntityBarrel;
import cn.nukkit.blockentity.BlockEntityNameable;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;

import java.util.Map;


public class BarrelInventory extends ContainerInventory implements BlockEntityInventoryNameable {
    public BarrelInventory(BlockEntityBarrel barrel) {
        super(barrel, InventoryType.CONTAINER, 27);
    }

    @Override
    public void init() {
        Map<Integer, ContainerSlotType> map = super.slotTypeMap();
        for (int i = 0; i < getSize(); i++) {
            map.put(i, ContainerSlotType.BARREL);
        }
    }

    @Override
    public BlockEntityBarrel getHolder() {
        return (BlockEntityBarrel) this.holder;
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);

        if (this.getViewers().size() == 1) {
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
        super.onClose(who);

        if (this.getViewers().isEmpty()) {
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
