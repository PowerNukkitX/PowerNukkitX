package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligentHuman;

/**
 * 这个Inventory是一个hack实现，用来实现{@link EntityIntelligentHuman}的背包实现，它无法被open 和 close，因为虚拟人类不会自己打开物品栏<p>
 * 它的{@link FakeHumanInventory#viewers}永远为空,因为不允许打开它
 */
@PowerNukkitXOnly
@Since("1.19.50-r3")
public class FakeHumanEnderChestInventory extends BaseInventory {
    public FakeHumanEnderChestInventory(EntityIntelligentHuman player) {
        super(player, InventoryType.ENDER_CHEST);
    }

    @Override
    public EntityIntelligentHuman getHolder() {
        return (EntityIntelligentHuman) this.holder;
    }

    //non
    @Override
    public void close(Player who) {
    }

    @Override
    public void onOpen(Player who) {
    }

    @Override
    public void onClose(Player who) {
    }

    @Override
    public boolean open(Player who) {
        return false;
    }
}
