package cn.nukkit.inventory;


import cn.nukkit.entity.item.EntityChestMinecart;

public class MinecartChestInventory extends ContainerInventory {

    public MinecartChestInventory(EntityChestMinecart minecart) {
        super(minecart, InventoryType.MINECART_CHEST, 27);
    }

    @Override
    public EntityChestMinecart getHolder() {
        return (EntityChestMinecart) this.holder;
    }

    @Override
    public boolean canCauseVibration() {
        return true;
    }
}
