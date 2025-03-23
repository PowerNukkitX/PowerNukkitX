package cn.nukkit.item;

public interface AliasItem {

    String getAliasIdentifier();

    default Item getItem() {
        return Item.get(getAliasIdentifier(), getDamage(), getCount(), getCompoundTag());
    }

    int getDamage();

    int getCount();

    byte[] getCompoundTag();

}
