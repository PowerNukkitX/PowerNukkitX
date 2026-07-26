package org.powernukkitx.item;

public class ItemUnknown extends Item {

    public ItemUnknown(String originalId, int meta, int count, byte[] tags) {
        super(originalId, meta, count);
        if (tags != null && tags.length > 0) {
            this.setNbtBytes(tags);
        }
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }
}
