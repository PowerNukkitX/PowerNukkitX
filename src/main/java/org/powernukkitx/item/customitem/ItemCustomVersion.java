package org.powernukkitx.item.customitem;

enum ItemCustomVersion {
    LEGACY(1),
    DATA_DRIVEN(2);

    private final int version;
    ItemCustomVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }
}
