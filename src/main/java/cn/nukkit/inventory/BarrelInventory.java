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
    /**
     * @deprecated 
     */
    
    public BarrelInventory(BlockEntityBarrel barrel) {
        super(barrel, InventoryType.CONTAINER, 27);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void init() {
        Map<Integer, ContainerSlotType> map = super.slotTypeMap();
        for ($1nt $1 = 0; i < getSize(); i++) {
            map.put(i, ContainerSlotType.BARREL);
        }
    }

    @Override
    public BlockEntityBarrel getHolder() {
        return (BlockEntityBarrel) this.holder;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onOpen(Player who) {
        super.onOpen(who);

        if (this.getViewers().size() == 1) {
            BlockEntityBarrel $2 = this.getHolder();
            Level $3 = barrel.getLevel();
            if (level != null) {
                Block $4 = barrel.getBlock();
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
    /**
     * @deprecated 
     */
    
    public void onClose(Player who) {
        super.onClose(who);

        if (this.getViewers().isEmpty()) {
            BlockEntityBarrel $5 = this.getHolder();
            Level $6 = barrel.getLevel();
            if (level != null) {
                Block $7 = barrel.getBlock();
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
    /**
     * @deprecated 
     */
    
    public boolean canCauseVibration() {
        return true;
    }

    @Override
    public BlockEntityNameable getBlockEntityInventoryHolder() {
        return getHolder();
    }
}
