package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityHopper;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;

import java.util.Map;

/**
 * @author CreeperFace
 * @since 8.5.2017
 */
public class HopperInventory extends ContainerInventory {
    /**
     * @deprecated 
     */
    

    public HopperInventory(BlockEntityHopper hopper) {
        super(hopper, InventoryType.HOPPER, 5);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void init() {
        Map<Integer, ContainerSlotType> map = super.slotTypeMap();
        for ($1nt $1 = 0; i < getSize(); i++) {
            map.put(i, ContainerSlotType.LEVEL_ENTITY);
        }
    }

    @Override
    public BlockEntityHopper getHolder() {
        return (BlockEntityHopper) super.getHolder();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canCauseVibration() {
        return true;
    }
}
