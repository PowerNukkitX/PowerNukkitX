package org.powernukkitx.blockentity;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockCampfire;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.event.inventory.CampfireSmeltEvent;
import org.powernukkitx.inventory.CampfireInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.recipe.CampfireRecipe;
import org.powernukkitx.utils.ItemHelper;

import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class BlockEntityCampfire extends BlockEntitySpawnable implements BlockEntityInventoryHolder {
    public BlockEntityCampfire(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    private static final int COOK_TIME = 600;

    private CampfireInventory inventory;
    private int[] itemTime;
    private CampfireRecipe[] recipes;
    private boolean[] keepItem;

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();
        scheduleUpdate();
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        this.inventory = new CampfireInventory(this);
        this.itemTime = new int[4];
        this.recipes = new CampfireRecipe[4];
        this.keepItem = new boolean[4];
        final CompoundTag nbtMap = getNbt();
        for (int i = 1; i <= itemTime.length; i++) {
            itemTime[i - 1] = nbtMap.getInt("ItemTime" + i);

            if (this.nbt.contains("Item" + i) && this.nbt.get("Item" + i) instanceof CompoundTag itemNBT) {
                inventory.setItemInternal(i - 1, ItemHelper.read(itemNBT));
            }
        }
    }

    protected CampfireRecipe findRecipe(Item item) {
        if (getBlock().getId().equals(BlockID.SOUL_CAMPFIRE)) {
            return this.server.getRecipeRegistry().findSoulCampfireRecipe(item);
        }

        return this.server.getRecipeRegistry().findCampfireRecipe(item);
    }

    @Override
    public boolean onUpdate() {
        boolean needsUpdate = false;
        Block block = getBlock();
        boolean isLit = block instanceof BlockCampfire && !((BlockCampfire) block).isExtinguished();

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            Item item = inventory.getItem(slot);

            if (item.isNull()) {
                itemTime[slot] = 0;
                recipes[slot] = null;
                keepItem[slot] = false;
                continue;
            }

            if (keepItem[slot]) {
                continue;
            }

            CampfireRecipe recipe = recipes[slot];

            if (recipe == null) {
                recipe = findRecipe(item);

                if (recipe == null) {
                    inventory.setItem(slot, Item.AIR);

                    ThreadLocalRandom random = ThreadLocalRandom.current();
                    this.level.dropItem(add(random.nextFloat(), 0.5, random.nextFloat()), item);

                    itemTime[slot] = 0;
                    recipes[slot] = null;
                    keepItem[slot] = false;
                    continue;
                }

                recipes[slot] = recipe;
            }

            if (itemTime[slot] < COOK_TIME) {
                if (!isLit) {
                    continue;
                }

                itemTime[slot]++;
                setDirty();
                needsUpdate = true;

                if (itemTime[slot] < COOK_TIME) {
                    continue;
                }
            }

            Item product = Item.get(
                    recipe.getResult().getId(),
                    recipe.getResult().getDamage(),
                    item.getCount()
            );

            CampfireSmeltEvent event = new CampfireSmeltEvent(this, item, product);
            this.server.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                inventory.setItem(slot, Item.AIR);

                ThreadLocalRandom random = ThreadLocalRandom.current();
                this.level.dropItem(
                        add(random.nextFloat(), 0.5, random.nextFloat()),
                        event.getResult()
                );

                itemTime[slot] = 0;
                recipes[slot] = null;
                keepItem[slot] = false;
            } else if (event.getKeepItem()) {
                keepItem[slot] = true;
                itemTime[slot] = COOK_TIME;
                recipes[slot] = null;
                setDirty();
            }
        }

        return needsUpdate;
    }

    public boolean getKeepItem(int slot) {
        if (slot < 0 || slot >= keepItem.length) {
            return false;
        }
        return keepItem[slot];
    }

    public void setKeepItem(int slot, boolean keep) {
        if (slot < 0 || slot >= keepItem.length) {
            return;
        }
        this.keepItem[slot] = keep;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        for (int i = 1; i <= itemTime.length; i++) {
            Item item = inventory.getItem(i - 1);
            if (item.isNull()) {
                this.nbt.remove("Item" + i);
                this.nbt.putInt("ItemTime" + i, 0);
            } else {
                this.nbt.putCompound("Item" + i, ItemHelper.write(item))
                        .putInt("ItemTime" + i, itemTime[i - 1]);
            }
        }
    }

    public void setRecipe(int index, CampfireRecipe recipe) {
        this.recipes[index] = recipe;
    }

    @Override
    public void close() {
        if (!closed) {
            for (Player player : new HashSet<>(this.getInventory().getViewers())) {
                player.removeWindow(this.getInventory());
            }
            super.close();
        }
    }

    @Override
    public void onBreak(boolean isSilkTouch) {
        for (Item content : inventory.getContents().values()) {
            level.dropItem(this, content);
        }
    }

    @Override
    public String getName() {
        return this.hasName() ? this.getNbt().getString("CustomName") : "Campfire";
    }

    @Override
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            nbt.remove("CustomName");
            return;
        }
        nbt.putString("CustomName", name);
    }

    @Override
    public boolean hasName() {
        return nbt.contains("CustomName");
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag builder = super.getSpawnCompound();

        for (int i = 1; i <= itemTime.length; i++) {
            Item item = inventory.getItem(i - 1);
            if (item.isNull()) {
                builder.remove("Item" + i);
            } else {
                builder.putCompound("Item" + i, ItemHelper.write(item, null));
            }
        }

        return builder;
    }

    @Override
    public boolean isBlockEntityValid() {
        final String blockId = getBlock().getId();
        return blockId.equals(BlockID.CAMPFIRE)
                || blockId.equals(BlockID.SOUL_CAMPFIRE);
    }

    public int getSize() {
        return 4;
    }

    public Item getItem(int index) {
        if (index < 0 || index >= getSize()) {
            return new ItemBlock(new BlockAir(), 0, 0);
        } else {
            CompoundTag data = this.getNbt().getCompound("Item" + (index + 1));
            return ItemHelper.read(data);
        }
    }

    public void setItem(int index, Item item) {
        if (index < 0 || index >= getSize()) {
            return;
        }

        CompoundTag nbt = ItemHelper.write(item);
        this.nbt.putCompound("Item" + (index + 1), nbt);
    }

    @Override
    public CampfireInventory getInventory() {
        return inventory;
    }
}
