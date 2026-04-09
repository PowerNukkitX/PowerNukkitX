package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockCampfire;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.inventory.CampfireSmeltEvent;
import cn.nukkit.inventory.CampfireInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.recipe.CampfireRecipe;
import cn.nukkit.utils.ItemHelper;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;

import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class BlockEntityCampfire extends BlockEntitySpawnable implements BlockEntityInventoryHolder {

    private CampfireInventory inventory;
    private int[] burnTime;
    private CampfireRecipe[] recipes;
    private boolean[] keepItem;


    public BlockEntityCampfire(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();
        scheduleUpdate();
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        this.inventory = new CampfireInventory(this);
        this.burnTime = new int[4];
        this.recipes = new CampfireRecipe[4];
        this.keepItem = new boolean[4];
        for (int i = 1; i <= burnTime.length; i++) {
            burnTime[i - 1] = namedTag.getInt("ItemTime" + i);
            keepItem[i - 1] = namedTag.getBoolean("KeepItem" + 1);

            if (this.namedTag.containsKey("Item" + i) && this.namedTag.get("Item" + i) instanceof NbtMap nbtMap) {
                inventory.setItem(i - 1, ItemHelper.read(nbtMap));
            }
        }
    }

    @Override
    public boolean onUpdate() {
        boolean needsUpdate = false;
        Block block = getBlock();
        boolean isLit = block instanceof BlockCampfire && !((BlockCampfire) block).isExtinguished();
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            Item item = inventory.getItem(slot);
            if (item.isNull()) {
                burnTime[slot] = 0;
                recipes[slot] = null;
            } else if (!keepItem[slot]) {
                CampfireRecipe recipe = recipes[slot];
                if (recipe == null) {
                    recipe = this.server.getRecipeRegistry().findCampfireRecipe(item);
                    if (recipe == null) {
                        inventory.setItem(slot, Item.AIR);
                        ThreadLocalRandom random = ThreadLocalRandom.current();
                        this.level.dropItem(add(random.nextFloat(), 0.5, random.nextFloat()), item);
                        burnTime[slot] = 0;
                        recipes[slot] = null;
                        continue;
                    } else {
                        burnTime[slot] = 600;
                        recipes[slot] = recipe;
                    }
                }

                int burnTimeLeft = burnTime[slot];
                if (burnTimeLeft <= 0) {
                    Item product = Item.get(recipe.getResult().getId(), recipe.getResult().getDamage(), item.getCount());
                    CampfireSmeltEvent event = new CampfireSmeltEvent(this, item, product);
                    if (!event.isCancelled()) {
                        inventory.setItem(slot, Item.AIR);
                        ThreadLocalRandom random = ThreadLocalRandom.current();
                        this.level.dropItem(add(random.nextFloat(), 0.5, random.nextFloat()), event.getResult());
                        burnTime[slot] = 0;
                        recipes[slot] = null;
                    } else if (event.getKeepItem()) {
                        keepItem[slot] = true;
                        burnTime[slot] = 0;
                        recipes[slot] = null;
                    }
                } else if (isLit) {
                    burnTime[slot]--;
                    needsUpdate = true;
                } else {
                    burnTime[slot] = 600;
                }
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
        for (int i = 1; i <= burnTime.length; i++) {
            Item item = inventory.getItem(i - 1);
            if (item == null || item.getId() == BlockID.AIR || item.getCount() <= 0) {
                namedTag.remove("Item" + i);
                this.namedTag = namedTag.toBuilder().putInt("ItemTime" + i, 0).build();
                namedTag.remove("KeepItem" + i);
            } else {
                this.namedTag = namedTag.toBuilder().putCompound("Item" + i, ItemHelper.write(item, null))
                        .putInt("ItemTime" + i, burnTime[i - 1])
                        .putBoolean("KeepItem" + i, keepItem[i - 1])
                        .build();
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
        return this.hasName() ? this.namedTag.getString("CustomName") : "Campfire";
    }

    @Override
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            namedTag.remove("CustomName");
            return;
        }
        this.namedTag = namedTag.toBuilder().putString("CustomName", name).build();
    }

    @Override
    public boolean hasName() {
        return namedTag.containsKey("CustomName");
    }

    @Override
    public NbtMap getSpawnCompound() {
        NbtMapBuilder c = super.getSpawnCompound().toBuilder();

        for (int i = 1; i <= burnTime.length; i++) {
            Item item = inventory.getItem(i - 1);
            if (item.isNull()) {
                c.remove("Item" + i);
            } else {
                c.putCompound("Item" + i, ItemHelper.write(item, null));
            }
        }

        return c.build();
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock().getId() == BlockID.CAMPFIRE;
    }

    public int getSize() {
        return 4;
    }

    public Item getItem(int index) {
        if (index < 0 || index >= getSize()) {
            return new ItemBlock(new BlockAir(), 0, 0);
        } else {
            NbtMap data = this.namedTag.getCompound("Item" + (index + 1));
            return ItemHelper.read(data);
        }
    }

    public void setItem(int index, Item item) {
        if (index < 0 || index >= getSize()) {
            return;
        }

        NbtMap nbt = ItemHelper.write(item, null);
        this.namedTag = this.namedTag.toBuilder().putCompound("Item" + (index + 1), nbt).build();
    }

    @Override
    public CampfireInventory getInventory() {
        return inventory;
    }
}
