package cn.nukkit.level.terra.delegate;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import com.dfsek.terra.api.inventory.Item;
import com.dfsek.terra.api.inventory.ItemStack;
import com.dfsek.terra.api.inventory.item.Damageable;
import com.dfsek.terra.api.inventory.item.ItemMeta;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public record PNXItemStack(cn.nukkit.item.Item innerItem) implements ItemStack, Damageable {
    @Override
    public int getAmount() {
        return innerItem.getCount();
    }

    @Override
    public void setAmount(int i) {
        innerItem.setCount(i);
    }

    @Override
    public Item getType() {
        return new PNXItemDelegate(innerItem);
    }

    @Override
    public ItemMeta getItemMeta() {
        return new PNXItemMeta(innerItem);
    }

    // TODO: 2022/2/14 确认setItemMeta的用途，当前实现可能造成附魔混乱
    @Override
    public void setItemMeta(ItemMeta itemMeta) {
        final var tmp = (PNXItemMeta) itemMeta;
        innerItem.addEnchantment(tmp.innerItem().getEnchantments());
    }

    @Override
    public cn.nukkit.item.Item getHandle() {
        return innerItem;
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public int getDamage() {
        return innerItem.getDamage();
    }

    @Override
    public void setDamage(int i) {
        innerItem.setDamage(i);
    }

    @Override
    public boolean hasDamage() {
        return innerItem.getDamage() != 0;
    }
}
