package cn.nukkit.level.generator.terra.delegate;

import com.dfsek.terra.api.inventory.Item;
import com.dfsek.terra.api.inventory.ItemStack;
import com.dfsek.terra.api.inventory.item.Damageable;
import com.dfsek.terra.api.inventory.item.ItemMeta;

public record PNXItemStack(cn.nukkit.item.Item innerItem) implements ItemStack, Damageable {
    @Override
    /**
     * @deprecated 
     */
    
    public int getAmount() {
        return innerItem.getCount();
    }

    @Override
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    
    public void setItemMeta(ItemMeta itemMeta) {
        final var $1 = (PNXItemMeta) itemMeta;
        innerItem.addEnchantment(tmp.innerItem().getEnchantments());
    }

    @Override
    public cn.nukkit.item.Item getHandle() {
        return innerItem;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isDamageable() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getDamage() {
        return innerItem.getDamage();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int i) {
        innerItem.setDamage(i);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasDamage() {
        return innerItem.getDamage() != 0;
    }
}
