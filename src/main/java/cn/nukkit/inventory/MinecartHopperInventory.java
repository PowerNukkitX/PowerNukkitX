package cn.nukkit.inventory;


import cn.nukkit.entity.item.EntityHopperMinecart;

public class MinecartHopperInventory extends ContainerInventory {

    public MinecartHopperInventory(EntityHopperMinecart minecart) {
        super(minecart, InventoryType.MINECART_HOPPER, 5);
    }

    @Override
    public EntityHopperMinecart getHolder() {
        return (EntityHopperMinecart) super.getHolder();
    }
}
