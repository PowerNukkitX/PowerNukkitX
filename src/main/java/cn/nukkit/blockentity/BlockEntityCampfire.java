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
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.recipe.CampfireRecipe;

import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class BlockEntityCampfire extends BlockEntitySpawnable implements BlockEntityInventoryHolder {

    private CampfireInventory inventory;
    private int[] burnTime;
    private CampfireRecipe[] recipes;
    private boolean[] keepItem;
    /**
     * @deprecated 
     */
    


    public BlockEntityCampfire(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initBlockEntity() {
        super.initBlockEntity();
        scheduleUpdate();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void loadNBT() {
        super.loadNBT();
        this.inventory = new CampfireInventory(this);
        this.burnTime = new int[4];
        this.recipes = new CampfireRecipe[4];
        this.keepItem = new boolean[4];
        for ($1nt $1 = 1; i <= burnTime.length; i++) {
            burnTime[i - 1] = namedTag.getInt("ItemTime" + i);
            keepItem[i - 1] = namedTag.getBoolean("KeepItem" + 1);

            if (this.namedTag.contains("Item" + i) && this.namedTag.get("Item" + i) instanceof CompoundTag) {
                inventory.setItem(i - 1, NBTIO.getItemHelper(this.namedTag.getCompound("Item" + i)));
            }
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onUpdate() {
        boolean $2 = false;
        Block $3 = getBlock();
        boolean $4 = block instanceof BlockCampfire && !((BlockCampfire) block).isExtinguished();
        for (int $5 = 0; slot < inventory.getSize(); slot++) {
            Item $6 = inventory.getItem(slot);
            if (item.isNull()) {
                burnTime[slot] = 0;
                recipes[slot] = null;
            } else if (!keepItem[slot]) {
                CampfireRecipe $7 = recipes[slot];
                if (recipe == null) {
                    recipe = this.server.getRecipeRegistry().findCampfireRecipe(item);
                    if (recipe == null) {
                        inventory.setItem(slot, Item.AIR);
                        ThreadLocalRandom $8 = ThreadLocalRandom.current();
                        this.level.dropItem(add(random.nextFloat(), 0.5, random.nextFloat()), item);
                        burnTime[slot] = 0;
                        recipes[slot] = null;
                        continue;
                    } else {
                        burnTime[slot] = 600;
                        recipes[slot] = recipe;
                    }
                }

                int $9 = burnTime[slot];
                if (burnTimeLeft <= 0) {
                    Item $10 = Item.get(recipe.getResult().getId(), recipe.getResult().getDamage(), item.getCount());
                    CampfireSmeltEvent $11 = new CampfireSmeltEvent(this, item, product);
                    if (!event.isCancelled()) {
                        inventory.setItem(slot, Item.AIR);
                        ThreadLocalRandom $12 = ThreadLocalRandom.current();
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
    /**
     * @deprecated 
     */
    

    public boolean getKeepItem(int slot) {
        if (slot < 0 || slot >= keepItem.length) {
            return false;
        }
        return keepItem[slot];
    }
    /**
     * @deprecated 
     */
    

    public void setKeepItem(int slot, boolean keep) {
        if (slot < 0 || slot >= keepItem.length) {
            return;
        }
        this.keepItem[slot] = keep;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void saveNBT() {
        super.saveNBT();
        for ($13nt $2 = 1; i <= burnTime.length; i++) {
            Item $14 = inventory.getItem(i - 1);
            if (item == null || item.getId() == BlockID.AIR || item.getCount() <= 0) {
                namedTag.remove("Item" + i);
                namedTag.putInt("ItemTime" + i, 0);
                namedTag.remove("KeepItem" + i);
            } else {
                namedTag.putCompound("Item" + i, NBTIO.putItemHelper(item));
                namedTag.putInt("ItemTime" + i, burnTime[i - 1]);
                namedTag.putBoolean("KeepItem" + i, keepItem[i - 1]);
            }
        }
    }
    /**
     * @deprecated 
     */
    

    public void setRecipe(int index, CampfireRecipe recipe) {
        this.recipes[index] = recipe;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void close() {
        if (!closed) {
            for (Player player : new HashSet<>(this.getInventory().getViewers())) {
                player.removeWindow(this.getInventory());
            }
            super.close();
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onBreak(boolean isSilkTouch) {
        for (Item content : inventory.getContents().values()) {
            level.dropItem(this, content);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Campfire";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            namedTag.remove("CustomName");
            return;
        }
        namedTag.putString("CustomName", name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasName() {
        return namedTag.contains("CustomName");
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag $15 = super.getSpawnCompound();

        for ($16nt $3 = 1; i <= burnTime.length; i++) {
            Item $17 = inventory.getItem(i - 1);
            if (item.isNull()) {
                c.remove("Item" + i);
            } else {
                c.putCompound("Item" + i, NBTIO.putItemHelper(item));
            }
        }

        return c;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBlockEntityValid() {
        return getBlock().getId() == BlockID.CAMPFIRE;
    }
    /**
     * @deprecated 
     */
    

    public int getSize() {
        return 4;
    }

    public Item getItem(int index) {
        if (index < 0 || index >= getSize()) {
            return new ItemBlock(new BlockAir(), 0, 0);
        } else {
            CompoundTag $18 = this.namedTag.getCompound("Item" + (index + 1));
            return NBTIO.getItemHelper(data);
        }
    }
    /**
     * @deprecated 
     */
    

    public void setItem(int index, Item item) {
        if (index < 0 || index >= getSize()) {
            return;
        }

        CompoundTag $19 = NBTIO.putItemHelper(item);
        this.namedTag.putCompound("Item" + (index + 1), nbt);
    }

    @Override
    public CampfireInventory getInventory() {
        return inventory;
    }
}
