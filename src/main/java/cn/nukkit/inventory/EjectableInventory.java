package cn.nukkit.inventory;


public abstract class EjectableInventory extends ContainerInventory implements BlockEntityInventoryNameable {
    public EjectableInventory(InventoryHolder holder, InventoryType type, int size) {
        super(holder, type, size);
    }
}
