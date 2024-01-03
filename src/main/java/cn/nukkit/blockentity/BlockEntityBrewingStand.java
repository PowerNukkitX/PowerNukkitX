package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBrewingStand;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.event.inventory.BrewEvent;
import cn.nukkit.event.inventory.StartBrewEvent;
import cn.nukkit.inventory.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.ContainerSetDataPacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class BlockEntityBrewingStand extends BlockEntitySpawnable implements RecipeInventoryHolder, BlockEntityContainer, BlockEntityNameable {
    public static final int MAX_BREW_TIME = 400;
    protected BrewingInventory inventory;
    public int brewTime;
    public int fuelTotal;
    public int fuelAmount;

    public BlockEntityBrewingStand(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();
        if (brewTime < MAX_BREW_TIME) {
            this.scheduleUpdate();
        }
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        inventory = new BrewingInventory(this);
        if (!namedTag.contains("Items") || !(namedTag.get("Items") instanceof ListTag)) {
            namedTag.putList(new ListTag<>("Items"));
        }

        for (int i = 0; i < getSize(); i++) {
            inventory.setItem(i, this.getItem(i));
        }

        if (!namedTag.contains("CookTime") || namedTag.getShort("CookTime") > MAX_BREW_TIME) {
            this.brewTime = MAX_BREW_TIME;
        } else {
            this.brewTime = namedTag.getShort("CookTime");
        }

        this.fuelAmount = namedTag.getShort("FuelAmount");
        this.fuelTotal = namedTag.getShort("FuelTotal");
    }

    @Override
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Brewing Stand";
    }

    @Override
    public boolean hasName() {
        return namedTag.contains("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.equals("")) {
            namedTag.remove("CustomName");
            return;
        }

        namedTag.putString("CustomName", name);
    }

    @Override
    public void close() {
        if (!closed) {
            for (Player player : new HashSet<>(getInventory().getViewers())) {
                player.removeWindow(getInventory());
            }
            super.close();
        }
    }

    @Override
    public void onBreak() {
        for (Item content : inventory.getContents().values()) {
            level.dropItem(this, content);
        }
        this.inventory.clearAll();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        namedTag.putList(new ListTag<>("Items"));
        for (int index = 0; index < getSize(); index++) {
            this.setItem(index, inventory.getItem(index));
        }

        namedTag.putShort("CookTime", brewTime);
        namedTag.putShort("FuelAmount", this.fuelAmount);
        namedTag.putShort("FuelTotal", this.fuelTotal);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock().getId() == BlockID.BREWING_STAND;
    }

    public int getSize() {
        return 5;
    }

    protected int getSlotIndex(int index) {
        ListTag<CompoundTag> list = this.namedTag.getList("Items", CompoundTag.class);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getByte("Slot") == index) {
                return i;
            }
        }

        return -1;
    }

    public Item getItem(int index) {
        int i = this.getSlotIndex(index);
        if (i < 0) {
            return new ItemBlock(Block.get(BlockID.AIR), 0, 0);
        } else {
            CompoundTag data = (CompoundTag) this.namedTag.getList("Items").get(i);
            return NBTIO.getItemHelper(data);
        }
    }

    public void setItem(int index, Item item) {
        int i = this.getSlotIndex(index);

        CompoundTag d = NBTIO.putItemHelper(item, index);

        if (item.getId() == BlockID.AIR || item.getCount() <= 0) {
            if (i >= 0) {
                this.namedTag.getList("Items").getAll().remove(i);
            }
        } else if (i < 0) {
            (this.namedTag.getList("Items", CompoundTag.class)).add(d);
        } else {
            (this.namedTag.getList("Items", CompoundTag.class)).add(i, d);
        }
    }

    @Override
    public BrewingInventory getInventory() {
        return inventory;
    }

    @Override
    public boolean onUpdate() {
        if (closed) {
            return false;
        }

        restockFuel();

        if (this.fuelAmount <= 0 || matchRecipes(true)[0] == null) {
            stopBrewing();
            return false;
        }
        
        if (brewTime == MAX_BREW_TIME) {
            StartBrewEvent e = new StartBrewEvent(this);
            this.server.getPluginManager().callEvent(e);

            if (e.isCancelled()) {
                return false;
            }

            this.sendBrewTime();
        }

        if (--brewTime > 0) {
            
            if (brewTime % 40 == 0) {
                sendBrewTime();
            }
            
            return true;
        }

        //20 seconds
        BrewEvent e = new BrewEvent(this);
        this.server.getPluginManager().callEvent(e);

        if (e.isCancelled()) {
            stopBrewing();
            return true;
        }
        
        boolean mixed = false;
        MixRecipe[] recipes = matchRecipes(false);
        for (int i = 0; i < 3; i++) {
            MixRecipe recipe = recipes[i];
            if (recipe == null) {
                continue;
            }

            Item previous = inventory.getItem(i + 1);
            if (!previous.isNull()) {
                Item result = recipe.getResult();
                result.setCount(previous.getCount());
                if (recipe instanceof ContainerRecipe) {
                    result.setAux(previous.getAux());
                }
                inventory.setItem(i + 1, result);
                mixed = true;
            }
        }
        
        if (mixed) {
            Item ingredient = this.inventory.getIngredient();
            ingredient.count--;
            this.inventory.setIngredient(ingredient);

            this.fuelAmount--;
            this.sendFuel();

            this.getLevel().addSound(this, Sound.RANDOM_POTION_BREWED);
        }
        
        stopBrewing();
        return true;
    }

    private void restockFuel() {
        Item fuel = this.getInventory().getFuel();
        if (this.fuelAmount > 0 || fuel.getId() != ItemID.BLAZE_POWDER || fuel.getCount() <= 0) {
            return;
        }
        
        fuel.count--;
        this.fuelAmount = 20;
        this.fuelTotal = 20;

        this.inventory.setFuel(fuel);
        this.sendFuel();
    }

    private void stopBrewing() {
        this.brewTime = 0;
        this.sendBrewTime();
        this.brewTime = MAX_BREW_TIME;
    }
    
    private MixRecipe[] matchRecipes(boolean quickTest) {
        MixRecipe[] recipes = new MixRecipe[quickTest? 1 : 3];
        Item ingredient = inventory.getIngredient();
        CraftingManager craftingManager = getLevel().getServer().getCraftingManager();
        for (int i = 0; i < 3; i++) {
            Item potion = inventory.getItem(i + 1);
            if (potion.isNull()) {
                continue;
            }
            
            MixRecipe recipe = craftingManager.matchBrewingRecipe(ingredient, potion);
            if (recipe == null) {
                recipe = craftingManager.matchContainerRecipe(ingredient, potion);
            } 
            if (recipe == null) {
                continue;
            }
            
            if (quickTest) {
                recipes[0] = recipe;
                return recipes;
            }
            
            recipes[i] = recipe;
        }
        
        return recipes;
    }

    protected void sendFuel() {
        ContainerSetDataPacket pk = new ContainerSetDataPacket();

        for (Player p : this.inventory.getViewers()) {
            int windowId = p.getWindowId(this.inventory);
            if (windowId > 0) {
                pk.windowId = windowId;

                pk.property = ContainerSetDataPacket.PROPERTY_BREWING_STAND_FUEL_AMOUNT;
                pk.value = this.fuelAmount;
                p.dataPacket(pk);

                pk.property = ContainerSetDataPacket.PROPERTY_BREWING_STAND_FUEL_TOTAL;
                pk.value = this.fuelTotal;
                p.dataPacket(pk);
            }
        }
    }

    protected void sendBrewTime() {
        ContainerSetDataPacket pk = new ContainerSetDataPacket();
        pk.property = ContainerSetDataPacket.PROPERTY_BREWING_STAND_BREW_TIME;
        pk.value = brewTime;

        for (Player p : this.inventory.getViewers()) {
            int windowId = p.getWindowId(this.inventory);
            if (windowId > 0) {
                pk.windowId = windowId;

                p.dataPacket(pk);
            }
        }
    }

    
    public void updateBlock() {
        Block block = this.getLevelBlock();

        if (!(block instanceof BlockBrewingStand blockBrewingStand)) {
            return;
        }

        for (int i = 1; i <= 3; ++i) {
            Item potion = this.inventory.getItem(i);

            String id = potion.getId();
            if ((id == ItemID.POTION || id == ItemID.SPLASH_POTION || id == ItemID.LINGERING_POTION) && potion.getCount() > 0) {
                switch (i){
                    case 1-> blockBrewingStand.setPropertyValue(CommonBlockProperties.BREWING_STAND_SLOT_A_BIT,true);
                    case 2-> blockBrewingStand.setPropertyValue(CommonBlockProperties.BREWING_STAND_SLOT_B_BIT,true);
                    case 3-> blockBrewingStand.setPropertyValue(CommonBlockProperties.BREWING_STAND_SLOT_C_BIT,true);
                }
            }else {
                switch (i){
                    case 1-> blockBrewingStand.setPropertyValue(CommonBlockProperties.BREWING_STAND_SLOT_A_BIT,false);
                    case 2-> blockBrewingStand.setPropertyValue(CommonBlockProperties.BREWING_STAND_SLOT_B_BIT,false);
                    case 3-> blockBrewingStand.setPropertyValue(CommonBlockProperties.BREWING_STAND_SLOT_C_BIT,false);
                }
            }
        }
        this.level.setBlock(block, block, false, false);
        
        if (brewTime != MAX_BREW_TIME && matchRecipes(true)[0] == null) {
            stopBrewing();
        }
    }

    public int getFuel() {
        return fuelAmount;
    }

    public void setFuel(int fuel) {
        this.fuelAmount = fuel;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag nbt = new CompoundTag()
                .putString("id", BlockEntity.BREWING_STAND)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putShort("FuelTotal", this.fuelTotal)
                .putShort("FuelAmount", this.fuelAmount);

        if (this.brewTime < MAX_BREW_TIME) {
            nbt.putShort("CookTime", this.brewTime);
        }

        if (this.hasName()) {
            nbt.put("CustomName", namedTag.get("CustomName"));
        }

        return nbt;
    }

    @Override
    public Inventory getIngredientView() {
        return new InventorySlice(this.inventory, 0, 1);
    }

    @Override
    public Inventory getProductView() {
        return new InventorySlice(this.inventory, 1, 4);
    }
}
