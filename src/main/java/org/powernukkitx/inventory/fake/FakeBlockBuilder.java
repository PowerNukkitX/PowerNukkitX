package org.powernukkitx.inventory.fake;

@FunctionalInterface
public interface FakeBlockBuilder {
    FakeBlock create(FakeInventory inventory);
}
