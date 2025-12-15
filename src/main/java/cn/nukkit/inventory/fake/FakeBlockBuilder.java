package cn.nukkit.inventory.fake;

@FunctionalInterface
public interface FakeBlockBuilder {
    FakeBlock create(FakeInventory inventory);
}
