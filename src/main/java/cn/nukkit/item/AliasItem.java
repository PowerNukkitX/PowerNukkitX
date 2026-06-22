package cn.nukkit.item;

public interface AliasItem {

    String getAliasIdentifier();

    default Item getItem() {
        return Item.get(getAliasIdentifier(), getDamage(), getCount(), getNbtBytes());
    }

    int getDamage();

    int getCount();

    byte[] getNbtBytes();

}
