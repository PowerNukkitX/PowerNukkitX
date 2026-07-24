package org.powernukkitx.item;

public class ItemUnknown extends Item {

    private final String originalId;

    public ItemUnknown(String originalId, int meta, int count, byte[] tags) {
        super(originalId, meta, count);
        this.originalId = originalId;
        if (tags != null && tags.length > 0) {
            this.setNbtBytes(tags);
        }
    }

    public String getOriginalId() {
        return originalId;
    }

    public int getMaxStackSize() {
        return 64;
    }
}
